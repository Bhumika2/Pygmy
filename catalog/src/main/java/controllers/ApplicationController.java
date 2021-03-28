/**
 * Copyright (C) 2012-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright (C) 2013 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright (C) 2013 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright (C) 2013 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import models.Book;
import models.UpdateResponse;
import ninja.Result;
import ninja.Results;

import com.google.inject.Singleton;
import ninja.params.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Singleton
public class ApplicationController {

    Logger logger = LoggerFactory.getLogger("Pygmy");
    private HashMap<Integer, Book> bookMap;
    private HashMap<String, List<Integer>> topicMap;
    private Connection connection;

    public ApplicationController() {
        setDBConnection();
        getAllBooks();
    }

    public Result queryByItem(@PathParam("id") int id) {
        logger.info("Query by Item request received for item: " + id);
        return Results.json().render(bookMap.get(id));
    }

    public Result queryBySubject(@PathParam("topic") String topic) throws UnsupportedEncodingException {
        topic = URLDecoder.decode(topic, StandardCharsets.UTF_8.toString());
        logger.info("Query by Subject request received for topic: " + topic);
        List<Integer> bookList = topicMap.get(topic);
        List<Book> booksByTopic = new ArrayList<>();
        for(Integer bookNumber: bookList){
            booksByTopic.add(bookMap.get(bookNumber));
        }
        return Results.json().render(booksByTopic);
    }

    public Result update(@PathParam("id") int id, @PathParam("type") String type) {
        logger.info(type + " update request received for item: " + id);
        String message = "failure";
        if(type.equals("restock")){
            restockBook(bookMap.get(id).getBookNumber());
            bookMap.get(id).setCount(5);
            message = "success";
        } else {
            synchronized (bookMap) {
                if (bookMap.get(id) != null) {
                    if (bookMap.get(id).getCount() > 0) {
                        bookMap.get(id).setCount(bookMap.get(id).getCount() - 1);
                        message = "success";
                    }
                }
            }
            if (message.equals("success")) {
                updateDB(bookMap.get(id).getBookNumber());
            }
        }
        UpdateResponse updateRes = new UpdateResponse();
        updateRes.setBookNumber(id);
        updateRes.setMessage(message);
        return Results.json().render(updateRes);
    }

    public void getAllBooks() {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery("select * from book");
            bookMap = new HashMap<>();
            topicMap = new HashMap<>();
            List<Integer> distributedBooks = new ArrayList<>();
            List<Integer> graduateBooks = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book(rs.getInt("book_number"), rs.getString("book_name"),
                        rs.getString("topic"), rs.getInt("cost"), rs.getInt("count"));
                bookMap.put(rs.getInt("book_number"), book);
                switch (rs.getString("topic")) {
                    case "distributed systems":
                        distributedBooks.add(rs.getInt("book_number"));
                        break;
                    case "graduate school":
                        graduateBooks.add(rs.getInt("book_number"));
                        break;
                }
            }
            topicMap.put("distributed systems", distributedBooks);
            topicMap.put("graduate school", graduateBooks);
            //System.out.println(bookMap);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void updateDB(Integer bookNumber) {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("update book set count = count - 1 where book_number = " + bookNumber);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void restockBook(Integer bookNumber) {
        logger.info("Restocking book - " + bookNumber);
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("update book set count = 5 where book_number = " + bookNumber);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public void setDBConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:sqlite:books.db");
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

}

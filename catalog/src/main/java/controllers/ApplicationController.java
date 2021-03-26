/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


@Singleton
public class ApplicationController {

    private HashMap<Integer,Book> bookMap;
    private HashMap<String,List<Book>> topicMap;

    public ApplicationController() {
        bookMap = new HashMap<>();
        bookMap.put(1, new Book(1,"How to get a good grade in 677 in 20 minutes a day","distributed systems",25,5));
        bookMap.put(2, new Book(2,"RPCs for Dummies","distributed systems",50,3));
        bookMap.put(3, new Book(3,"Xen and the Art of Surviving Graduate School","graduate school",40,5));
        bookMap.put(4, new Book(4,"Cooking for the Impatient Graduate Student","graduate school",60,3));

        topicMap = new HashMap<>();
        List<Book> books = new ArrayList<>();
        books.add(new Book(1,"How to get a good grade in 677 in 20 minutes a day","distributed systems",25,5));
        books.add(new Book(2,"RPCs for Dummies","distributed systems",50,3));
        topicMap.put("distributed systems",books);
        books = new ArrayList<>();
        books.add(new Book(3,"Xen and the Art of Surviving Graduate School","graduate school",40,5));
        books.add(new Book(4,"Cooking for the Impatient Graduate Student","graduate school",60,3));
        topicMap.put("graduate school",books);
    }
    public Result queryByItem(@PathParam("id") int id)
    {
        System.out.println("Query by Item request received for item: "+id);
        return Results.json().render(bookMap.get(id));
    }
    public Result queryBySubject(@PathParam("topic") String topic) throws UnsupportedEncodingException {
        topic = URLDecoder.decode(topic, StandardCharsets.UTF_8.toString());
        System.out.println("Query by Subject request received for topic: "+topic);
        return Results.json().render(topicMap.get(topic));
    }

    public Result update(@PathParam("id") int id) {
        System.out.println("Update request received for item: "+id);
        String message = "failure";
        if(bookMap.get(id)!=null && bookMap.get(id).getCount() > 0) {
            bookMap.get(id).setCount(bookMap.get(id).getCount() - 1);
            message = "success";
        }
        UpdateResponse updateRes = new UpdateResponse();
        updateRes.setId(id);
        updateRes.setMessage(message);
        return Results.json().render(updateRes);
    }

    //TODO periodically update the stock items
}

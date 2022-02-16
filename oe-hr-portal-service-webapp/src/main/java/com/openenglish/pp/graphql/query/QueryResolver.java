package com.openenglish.pp.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;

@DgsComponent
public class QueryResolver {

    @DgsData(parentType = "Query", field = "sayHello")
    public String sayHello() {
        return "Hi there, welcome to graphql on OpenEnglish";
    }
}

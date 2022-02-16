package com.openenglish.pp.graphql.mutation;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;


@DgsComponent
public class MutationResolver {

    @DgsData(parentType = "Mutation", field = "reflectMyName")
    public String reflectMyName(@InputArgument(value="name") String name){
        if(name.length()<3){
            throw new com.netflix.graphql.dgs.exceptions.DgsInvalidInputArgumentException("Name too short",null);
        }
        return String.format("you type %s as your name", name);
    }
}

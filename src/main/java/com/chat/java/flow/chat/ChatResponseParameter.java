package com.chat.java.flow.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseParameter {
   private String id;
   private String object;
   private String created;
   private String model;

   private List<Choice> choices;
}

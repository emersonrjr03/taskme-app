package com.herms.taskme.controller;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.herms.taskme.dto.MessageDTO;
import com.herms.taskme.model.Conversation;
import com.herms.taskme.model.Message;
import com.herms.taskme.model.User;
import com.herms.taskme.service.ConversationService;
import com.herms.taskme.service.CustomUserDetailsService;
import com.herms.taskme.service.MessageService;

@RestController
@RequestMapping("/logged")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @RequestMapping(method = RequestMethod.GET, value = "/message/{conversationId}")
    public ResponseEntity<Page<MessageDTO>> getMessages(@PathVariable Long conversationId,
                                                                       @RequestParam(value="page", defaultValue = "0") Integer pageNumber,
                                                                       @RequestParam(value="linesPerPage", defaultValue = "4") Integer linesPerPage,
                                                                       @RequestParam(value="orderBy", defaultValue = "id") String orderBy,
                                                                       @RequestParam(value="direction", defaultValue = "DESC") String direction,
                                                                       @RequestParam(value="searchTerm", defaultValue = "") String searchTerm) throws Exception {

    	User principal = customUserDetailsService.getLoggedUser();
        Conversation conversation = conversationService.getConversation(conversationId);
        if(conversation == null || !conversation.getUserList().contains(principal)){
            throw new Exception("You don't have access to this function");
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<Message> convMessagesList = messageService.getMessagesFromConvPaginated(pageRequest, conversationId);
        
        return new ResponseEntity<>(convMessagesList.map(MessageDTO::new), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/message")
    public ResponseEntity<MessageDTO> createMessageWith(@RequestBody Message message) throws Exception {

        User principal = customUserDetailsService.getLoggedUser();
        
        if(principal == null ||  !message.getSender().equals(principal.getId())){
            throw new Exception("You don't have access to this function");
        }
        
        return new ResponseEntity<>(new MessageDTO(messageService.addMessage(message)), HttpStatus.OK);

    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/message/send/{targetUserId}")
    public ResponseEntity<MessageDTO> sendMessageTo(@PathVariable Long targetUserId, @RequestBody MessageDTO message) throws Exception {

        User principal = customUserDetailsService.getLoggedUser();
        
        if(principal == null ||  principal.getId().equals(targetUserId)){
            throw new Exception("You don't have access to this function");
        }
        message.setUserSenderId(principal.getId());
        return new ResponseEntity<>(new MessageDTO(messageService.sendMessageTo(targetUserId, message)), HttpStatus.OK);

    }
}

package com.damian.whatsapp.modules.chat.service;

import com.damian.whatsapp.modules.chat.ChatType;
import com.damian.whatsapp.modules.chat.dto.ChatMessageRequest;
import com.damian.whatsapp.modules.chat.dto.ChatMessageResponse;
import com.damian.whatsapp.modules.group.group.exception.GroupNotFoundException;
import com.damian.whatsapp.modules.group.group.repository.GroupRepository;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserPrincipal;
import com.damian.whatsapp.shared.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;


@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public ChatService(
            SimpMessagingTemplate messagingTemplate,
            GroupRepository groupRepository,
            UserRepository userRepository
    ) {
        this.messagingTemplate = messagingTemplate;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public void handle(ChatMessageRequest request, Principal principal) {
        log.debug("request {}", request);


        UserPrincipal
                userPrincipal
                = ((UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal());
        User currentUser = userPrincipal.getUser();


        ChatMessageResponse response = new ChatMessageResponse(
                request.chatType(),
                request.toId(),
                currentUser.getId(),
                currentUser.getUserName(),
                request.message(),
                Instant.now()
        );

        log.debug("response {}", response);

        if (request.chatType().equals(ChatType.GROUP)) {
            // TODO best use existsById
            Group group = groupRepository.findById(request.toId()).orElseThrow(
                    () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND, request.toId())
            );
            String destination = this.getDestination(request);
            log.debug("destination {}", destination);
            messagingTemplate.convertAndSend(destination, response);
            return;
        }

        if (request.chatType().equals(ChatType.PRIVATE)) {
            User toUser = userRepository.findById(request.toId()).orElseThrow(
                    () -> new UserNotFoundException(Exceptions.USER.NOT_FOUND, request.toId())
            );
            String destination = "/queue/messages";
            log.debug("destination {}", destination);
            log.debug("from {} to {}", principal.getName(), toUser.getEmail());
            messagingTemplate.convertAndSendToUser(toUser.getEmail(), destination, response);
        }
    }

    public String getDestination(ChatMessageRequest request) {
        if (request.chatType().equals(ChatType.GROUP)) {
            return "/topic/chat/" + request.chatType() + "/" + request.toId();
        }

        return "/chat/" + request.chatType() + "/" + request.toId();
    }
}

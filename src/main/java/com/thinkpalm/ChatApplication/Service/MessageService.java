package com.thinkpalm.ChatApplication.Service;

import com.thinkpalm.ChatApplication.Model.*;
import com.thinkpalm.ChatApplication.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class MessageService {

    private final UserRepository userRepository;

    private final MessageRepository messageRepository;

    private final MessageReceiverRepository messageReceiverRepository;
    private final RoomRepository roomRepository;
    private final MessageRoomRepository messageRoomRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository,UserRepository userRepository,MessageReceiverRepository messageReceiverRepository,RoomRepository roomRepository,MessageRoomRepository messageRoomRepository){
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageReceiverRepository = messageReceiverRepository;
        this.roomRepository = roomRepository;
        this.messageRoomRepository = messageRoomRepository;
    }

    public String sendMessage(MessageSendRequest messageSendRequest){
        Message message = messageSendRequest.getMessage();
        Receiver receiver = messageSendRequest.getReceiver();
        if("user".equals(receiver.getType())){
            UserModel receiverUser = userRepository.findByName(receiver.getName()).orElse(null);
            if(receiverUser != null){
                MessageReceiverModel messageReceiverModel = new MessageReceiverModel();
                messageReceiverModel.setReceiver(receiverUser);
                messageReceiverModel.setMessage(saveMessage(message));
                messageReceiverModel.setReceived_at(Timestamp.valueOf(LocalDateTime.now()));
                messageReceiverRepository.save(messageReceiverModel);

                return "message send successfully";

            }else{
                return "receiver not found!";
            }
        }
        else if("room".equals(receiver.getType())){
            RoomModel receiverRoom = roomRepository.findByName(receiver.getName());
            if(receiverRoom != null){
                MessageRoomModel messageRoomModel = new MessageRoomModel();
                messageRoomModel.setRoom(receiverRoom);
                messageRoomModel.setMessage(saveMessage(message));
                messageRoomModel.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
                messageRoomRepository.save(messageRoomModel);

                return "message send successfully";

            }else{
                return "room not found!";
            }
        }else{
            return "invalid receiverType!";
        }
    }
    public MessageModel saveMessage(Message message){
        Optional<UserModel> sender = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        MessageModel messageModel = new MessageModel();
        messageModel.setContent(message.getContent());
        messageModel.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        messageModel.setSender(sender.get());
        if(message.getParentMessage() != null){
            MessageModel parentMessage = messageRepository.findById(message.getParentMessage()).orElse(null);
            if(parentMessage != null){
                messageModel.setParent_message(parentMessage);
            }
        }
        messageRepository.save(messageModel);
        return messageModel;
    }
}

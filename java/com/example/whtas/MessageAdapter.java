package com.example.whtas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>

{



    private List<Message> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;


    public MessageAdapter (List<Message> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public TextView senderMessageDate, receiverMessageDate;
        public TextView senderPicDate, receiverPicDate;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);

            senderMessageDate = (TextView) itemView.findViewById(R.id.sender_date);
            receiverMessageDate = (TextView) itemView.findViewById(R.id.receiver_date);

            senderPicDate = (TextView) itemView.findViewById(R.id.sender_pic_date);
            receiverPicDate = (TextView) itemView.findViewById(R.id.receiver_pic_date);

        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int position)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Message messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);

        messageViewHolder.senderMessageDate.setVisibility(View.GONE);
        messageViewHolder.receiverMessageDate.setVisibility(View.GONE);


        messageViewHolder.senderPicDate.setVisibility(View.GONE);
        messageViewHolder.receiverPicDate.setVisibility(View.GONE);



        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageDate.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.WHITE);
                messageViewHolder.senderMessageText.setText(messages.getMessage());

                messageViewHolder.senderMessageDate.setText(messages.getTime() + "-" + messages.getDate());
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageDate.setVisibility(View.VISIBLE);


                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messagr_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.WHITE);
                messageViewHolder.receiverMessageText.setText(messages.getMessage());

                messageViewHolder.receiverMessageDate.setText(messages.getTime() + "-" + messages.getDate());
            }
        }
        else if (fromMessageType.equals("image"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                //messageViewHolder.senderPicDate.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

                //messageViewHolder.senderPicDate.setText("\n"+messages.getTime() + "-" + messages.getDate());
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
              //  messageViewHolder.receiverPicDate.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);

               // messageViewHolder.receiverPicDate.setText("\n"+messages.getTime() + "-" + messages.getDate());
            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if (fromUserID.equals(messageSenderId)){
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                //   messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/chatapp-52094.appspot.com/o/file.png?alt=media&token=b15b9a91-6632-462b-a67f-ab3ec8a7f975")
                        .into(messageViewHolder.messageSenderPicture);
          /*      messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });



           */
            }
            else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                //messageViewHolder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/chatapp-52094.appspot.com/o/file.png?alt=media&token=b15b9a91-6632-462b-a67f-ab3ec8a7f975")
                        .into(messageViewHolder.messageReceiverPicture);

            /*    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });


             */
            }
        }

        if (fromUserID.equals(messageSenderId)){
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMessagesList.get(position).getType().equals("pdf")|| userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[]=new CharSequence[]{
                                "Delete for me",
                                "Download and View This Document",
                                "Cancel",
                                "Delete for Everyone"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if(i==0){
                                    deleteSentMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else
                                if(i==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                                else
                                if(i==3)
                                {
                                    deleteMessageForEveryone(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }

                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[]=new CharSequence[]{
                                "Delete for me",
                                "Cancel",
                                "Delete for Everyone"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if(i==0){

                                    deleteSentMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);


                                } else
                                if(i==2)
                                {
                                    deleteMessageForEveryone(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(position).getType().equals("image")){
                        CharSequence options[]=new CharSequence[]{
                                "Delete for me",
                                "View This Image",
                                "Cancel",
                                "Delete for Everyone"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if(i==0){
                                    deleteSentMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else
                                if(i==1)
                                {
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else
                                if(i==3)
                                {
                                    deleteMessageForEveryone(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        else {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMessagesList.get(position).getType().equals("pdf")|| userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[]=new CharSequence[]{

                                "Delete for me",
                                "Download and View This Document",
                                "Cancel"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if(i==0){
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                                else
                                if(i==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[]=new CharSequence[]{
                                "Delete for me",
                                "Cancel"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if(i==0){
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(position).getType().equals("image")){
                        CharSequence options[]=new CharSequence[]{
                                "Delete for me",
                                "View This Image",
                                "Cancel"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if(i==0){

                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else
                                if(i==1)
                                {
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

    private void deleteSentMessage(final int positon,final  MessageViewHolder holder){
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(positon).getFrom())
                .child(userMessagesList.get(positon).getTo())
                .child(userMessagesList.get(positon).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(),"Deleted successfully",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(),"Error Occured",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void deleteReceiveMessage(final int positon,final  MessageViewHolder holder){
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(positon).getTo())
                .child(userMessagesList.get(positon).getFrom())
                .child(userMessagesList.get(positon).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(),"Deleted successfully",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(),"Error Occured",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void deleteMessageForEveryone(final int positon,final  MessageViewHolder holder){
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(positon).getTo())
                .child(userMessagesList.get(positon).getFrom())
                .child(userMessagesList.get(positon).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    rootRef.child("Messages")
                            .child(userMessagesList.get(positon).getFrom())
                            .child(userMessagesList.get(positon).getTo())
                            .child(userMessagesList.get(positon).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(holder.itemView.getContext(),"Deleted Successfuly",Toast.LENGTH_SHORT).show();


                        }
                    });
                }else {
                    Toast.makeText(holder.itemView.getContext(),"Error Occured",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }



}


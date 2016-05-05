--Code to delete tables for purposes of testing the create_tables code

DROP Trigger user_id_insert;
DROP Trigger msg_id_insert;
DROP Trigger group_id_insert;
DROP Trigger user_delete;
DROP Trigger group_membership_check;

DROP SEQUENCE user_seq;
DROP SEQUENCE group_seq;
DROP SEQUENCE msg_seq;

DROP TABLE Users CASCADE CONSTRAINTS;
DROP TABLE Friendships CASCADE CONSTRAINTS;
DROP TABLE User_groups CASCADE CONSTRAINTS;
DROP TABLE Messages CASCADE CONSTRAINTS;
DROP TABLE Group_membership CASCADE CONSTRAINTS;
DROP TABLE Recipients CASCADE CONSTRAINTS; 

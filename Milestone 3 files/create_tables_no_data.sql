@cleanup.sql;

-- Required to use our input data without TO_DATE AND TO_TIMESTAMP
--SELECT * FROM nls_session_parameters WHERE parameter = 'NLS_TIMESTAMP_FORMAT';
--SELECT * FROM nls_session_parameters WHERE parameter = 'NLS_DATE_FORMAT';
ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD';
ALTER SESSION SET NLS_TIMESTAMP_FORMAT ='YYYY-MM-DD HH24:MI:SS';
------------------------------------------------------------------------------------------------------------------------
-- CREATE TABLES                                                                                                      --
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Users
(
  user_ID    NUMBER(10) PRIMARY KEY, -- uid is a reserved word in oracle
  fname      VARCHAR2(32) NOT NULL,
  lname      VARCHAR2(32) NOT NULL,
  email      VARCHAR2(32) NOT NULL,
  dob        DATE         NOT NULL,
  last_login TIMESTAMP
);

CREATE SEQUENCE user_seq start with 1 increment by 1;

CREATE OR REPLACE TRIGGER user_id_insert 
BEFORE INSERT ON Users
FOR EACH ROW
BEGIN
  IF :new.user_ID!=0 THEN
	  SELECT user_seq.NEXTVAL
	  INTO   :new.user_ID
	  FROM   dual;
  END IF;
END;
/

insert into users values (0, 'Deleted user', 'Deleted user', 'Deleted user', '1970-01-01', null);

CREATE TABLE Friendships
(
  id_1         NUMBER(10),
  id_2         NUMBER(10),
  status       CHAR NOT NULL, -- there isnt really a bool type in oracle so youre supposed to use a char with val 0 or 1
  date_started TIMESTAMP, --could be null if not established
  CONSTRAINT Friendships_id1_id2_pk PRIMARY KEY (id_1, id_2),
  CONSTRAINT Friendships_FK_user1 FOREIGN KEY (id_1) REFERENCES Users (user_ID) ON DELETE CASCADE,
  CONSTRAINT Friendships_FK_user2 FOREIGN KEY (id_2) REFERENCES Users (user_ID) ON DELETE CASCADE
);


CREATE TABLE User_groups --group is a reserved word in oracle
(
  group_ID     NUMBER(10) PRIMARY KEY,
  name    VARCHAR(32) NOT NULL UNIQUE,
  descrip VARCHAR(100),
  limit number(10) Default 50 --Added a default so we can use with old files
);

CREATE SEQUENCE group_seq start with 1 increment by 1;

CREATE OR REPLACE TRIGGER group_id_insert 
BEFORE INSERT ON User_groups 
FOR EACH ROW
BEGIN
  SELECT group_seq.NEXTVAL
  INTO   :new.group_ID
  FROM   dual;
END;
/


CREATE TABLE Messages
(
  msg_ID    NUMBER(10) PRIMARY KEY,
  sender_ID NUMBER(10) NOT NULL,
  time_sent TIMESTAMP NOT NULL,
  subject   VARCHAR2(128) NOT NULL,
  body      VARCHAR2(1024) NOT NULL,
  CONSTRAINT Messages_FK_Contacts FOREIGN KEY (sender_ID) REFERENCES Users (user_ID),
  CONSTRAINT No_Spam_Messages UNIQUE(sender_ID, time_sent)
);

CREATE SEQUENCE msg_seq start with 1 increment by 1;

CREATE OR REPLACE TRIGGER msg_id_insert 
BEFORE INSERT ON Messages
FOR EACH ROW
BEGIN
  SELECT msg_seq.NEXTVAL
  INTO   :new.msg_ID
  FROM   dual;
END;
/


CREATE TABLE Recipients
(
  msg_ID  NUMBER(10),
  user_ID NUMBER(10),
  CONSTRAINT Recipients_PK PRIMARY KEY (msg_ID, user_ID),
  CONSTRAINT Recipients_FK_Users FOREIGN KEY (user_ID) REFERENCES Users (user_ID),
  CONSTRAINT Recipients_FK_Messages FOREIGN KEY (msg_ID) REFERENCES Messages (msg_ID)
);


CREATE TABLE Group_membership
(
  user_ID NUMBER(10),
  group_ID NUMBER(10),
  CONSTRAINT Group_membership_PK PRIMARY KEY (user_ID, group_ID),
  CONSTRAINT Group_membership_FK_User_ID FOREIGN KEY (user_ID) REFERENCES Users (user_ID),
  CONSTRAINT Group_membership_FK_Group_ID FOREIGN KEY (group_ID) REFERENCES User_groups (group_ID)
);

CREATE OR REPLACE TRIGGER group_membership_check
BEFORE INSERT ON Group_membership
FOR EACH ROW
DECLARE
	member_count INTEGER:=50;
	max_count INTEGER:=50;
BEGIN
    SELECT COUNT(*) INTO member_count FROM Group_membership WHERE group_ID=:new.group_ID;
	SELECT limit INTO max_count FROM User_groups WHERE User_groups.group_ID=:new.group_ID;
	IF member_count=max_count THEN
		raise_application_error(-20998, 'Group max membership exceeded');
	END IF;
END;
/

/*
* On delete with cascade, we will delete any group membership entries or recieved messages
*/
CREATE OR REPLACE TRIGGER user_delete
BEFORE DELETE ON Users
FOR EACH ROW
DECLARE
	member_count INTEGER;
	max_count INTEGER;
BEGIN
  --delete from group, as per project specs
  DELETE FROM Group_membership
  WHERE user_ID=:old.user_ID;

  --delete all recipients entries
  DELETE FROM Recipients WHERE user_ID=:old.user_ID;
  
  --Mark the sender as deleted
  UPDATE Messages
  SET sender_ID=0
  WHERE sender_ID=:old.user_ID;
  
  --if all recipients deleted, delete message
  DELETE FROM Messages
  WHERE Messages.msg_ID NOT IN (Select Recipients.msg_ID FROM Recipients) AND sender_ID=0;

END;
/



--@Users.sql;
--@Friendship.sql;
--@Messages.sql;
--@groups.sql;
--@group_membership.sql;
--@recipients.sql;


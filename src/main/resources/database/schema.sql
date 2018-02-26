create table ACCOUNT
(
  ID          bigint auto_increment,
  FULLNAME    varchar(255)   not null,
  AMOUNT      DECIMAL(20, 2) not null,
  CURRENCY    varchar(3)     not null,
  CREATE_TIME TIMESTAMP         not null
);

create table TRANSFER
(
  ID           bigint auto_increment,
  SOURCE_ID    bigint         not null,
  RECIPIENT_ID bigint         not null,
  AMOUNT       DECIMAL(20, 2) not null,
  CURRENCY     varchar(3)     not null,
  CREATE_TIME  TIMESTAMP      not null,
);


-- Löschen der Tabellen, falls vorhanden
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_PARAMS;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_INSTANCE;

-- Löschen der Sequenzen, falls vorhanden
DROP SEQUENCE IF EXISTS BATCH_STEP_EXECUTION_SEQ;
DROP SEQUENCE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
DROP SEQUENCE IF EXISTS BATCH_JOB_INSTANCE_SEQ;


-- Sequenzen
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_INSTANCE_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;

-- Tabellen
CREATE TABLE BATCH_JOB_INSTANCE (
                                    JOB_INSTANCE_ID BIGINT PRIMARY KEY,
                                    VERSION BIGINT,
                                    JOB_NAME VARCHAR(100) NOT NULL,
                                    JOB_KEY VARCHAR(320) NOT NULL,
                                    CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION (
                                     JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                     VERSION BIGINT,
                                     JOB_INSTANCE_ID BIGINT NOT NULL,
                                     CREATE_TIME TIMESTAMP DEFAULT NULL,
                                     START_TIME TIMESTAMP DEFAULT NULL,
                                     END_TIME TIMESTAMP DEFAULT NULL,
                                     STATUS VARCHAR(100),
                                     EXIT_CODE VARCHAR(2500),
                                     EXIT_MESSAGE VARCHAR(2500),
                                     LAST_UPDATED TIMESTAMP,
                                     JOB_CONFIGURATION_LOCATION VARCHAR(2500),
                                     FOREIGN KEY (JOB_INSTANCE_ID) REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);


CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
                                            JOB_EXECUTION_ID BIGINT NOT NULL,
                                            PARAMETER_NAME VARCHAR(100) NOT NULL,
                                            PARAMETER_TYPE VARCHAR(60) NOT NULL,
                                            PARAMETER_VALUE VARCHAR(250),
                                            IDENTIFYING CHAR(1) NOT NULL,
                                            FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION (
                                      STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                      VERSION BIGINT NOT NULL,
                                      STEP_NAME VARCHAR(100) NOT NULL,
                                      JOB_EXECUTION_ID BIGINT NOT NULL,
                                      START_TIME TIMESTAMP DEFAULT NULL,
                                      END_TIME TIMESTAMP DEFAULT NULL,
                                      STATUS VARCHAR(10),
                                      COMMIT_COUNT BIGINT,
                                      READ_COUNT BIGINT,
                                      FILTER_COUNT BIGINT,
                                      WRITE_COUNT BIGINT,
                                      READ_SKIP_COUNT BIGINT,
                                      WRITE_SKIP_COUNT BIGINT,
                                      PROCESS_SKIP_COUNT BIGINT,
                                      ROLLBACK_COUNT BIGINT,
                                      EXIT_CODE VARCHAR(2500),
                                      EXIT_MESSAGE VARCHAR(2500),
                                      LAST_UPDATED TIMESTAMP,
                                      CREATE_TIME TIMESTAMP DEFAULT NULL,
                                      FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
                                             JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                             SERIALIZED_CONTEXT CLOB,
                                             FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
                                              STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                              SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                              SERIALIZED_CONTEXT CLOB,
                                              FOREIGN KEY (STEP_EXECUTION_ID) REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
);

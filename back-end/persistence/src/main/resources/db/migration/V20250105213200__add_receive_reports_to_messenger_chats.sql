ALTER TABLE messenger_chats ADD COLUMN receive_reports BOOLEAN DEFAULT TRUE;
UPDATE messenger_chats SET receive_reports = TRUE WHERE receive_reports IS NULL;

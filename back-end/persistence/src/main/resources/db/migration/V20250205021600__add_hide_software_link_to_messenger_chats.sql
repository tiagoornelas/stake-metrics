ALTER TABLE messenger_chats ADD COLUMN hide_software_link BOOLEAN DEFAULT FALSE;
UPDATE messenger_chats SET hide_software_link = FALSE WHERE hide_software_link IS NULL;

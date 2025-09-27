INSERT INTO public.users (username,first_name,last_name,phone,birthdate,gender,image_filename,created_at,updated_at) VALUES
	 ('DamianS7','Damian','J.','701 444 113','1987-06-02','FEMALE'::public."user_gender_type",NULL ,'2025-04-28 01:47:10.771533', '2025-04-28 01:47:10.771533'),
	 ('AliceWhite','Alice','White','701 444 113','1987-06-02','FEMALE'::public."user_gender_type",NULL ,'2025-04-28 01:47:10.771533', '2025-04-28 01:47:10.771533'),
	 ('David_B1', 'David','Brown','901 322 223','1993-07-04','MALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533'),
	 ('Alxa1','Alexa','Brown','901 322 223','1993-07-04','FEMALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533'),
	 ('Alana81','Alana','Brown','901 322 223','1993-07-04','FEMALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533'),
	 ('Robert.Brown','Robert','Brown','901 322 223','1993-07-04','MALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533'),
	 ('Angela_SX','Angela','Brown','901 322 223','1993-07-04','FEMALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533'),
	 ('Thom.b4','Thomas','Brown','901 322 223','1993-07-04','MALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533'),
	 ('MLRX5','Michael','Brown','901 322 223','1993-07-04','MALE'::public."user_gender_type",NULL ,'2025-04-28 01:48:28.903419', '2025-04-28 01:47:10.771533');
INSERT INTO public.user_accounts (user_id, email,"role", password_hash,account_status, updated_at) VALUES
	 (1, 'damian@demo.com','ADMIN'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (2, 'alice@demo.com','USER'::public."user_role_type" , '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (3, 'david@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (4, 'alexa@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (5, 'alana@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (6, 'robert@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (7, 'angela@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (8, 'thomas@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616'),
	 (9, 'michael@demo.com','USER'::public."user_role_type", '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','VERIFIED'::public."account_status_type", '2025-04-06 02:42:27.378616');
INSERT INTO public.user_settings (user_id,setting_key,setting_value) VALUES
(1, 'lang','es'),
(2, 'lang','es'),
(3, 'lang','es'),
(4, 'lang','es'),
(5, 'lang','es'),
(6, 'lang','es'),
(7, 'lang','es'),
(8, 'lang','es'),
(9, 'lang','es');
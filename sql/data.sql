INSERT INTO public.customers (email,"role",created_at,updated_at) VALUES
	 ('admin@demo.com','ADMIN'::public."customer_role_type",'2025-04-17 02:07:41.382291','2025-04-17 02:07:41.382291'),
	 ('alice@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('david@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('alexa@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('alana@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('robert@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('angela@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('thomas@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('michael@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616');
INSERT INTO public.customer_profiles (customer_id,first_name,last_name,phone,birthdate,gender,avatar_filename,updated_at) VALUES
	 (1,'Damian','J.','701 444 113','1987-06-02','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:47:10.771533'),
	 (2,'Alice','White','701 444 113','1987-06-02','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:47:10.771533'),
	 (3,'David','Brown','901 322 223','1993-07-04','MALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419'),
	 (4,'Alexa','Brown','901 322 223','1993-07-04','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419'),
	 (5,'Alana','Brown','901 322 223','1993-07-04','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419'),
	 (6,'Robert','Brown','901 322 223','1993-07-04','MALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419'),
	 (7,'Angela','Brown','901 322 223','1993-07-04','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419'),
	 (8,'Thomas','Brown','901 322 223','1993-07-04','MALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419'),
	 (9,'Michael','Brown','901 322 223','1993-07-04','MALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419');
INSERT INTO public.customer_auth (customer_id,password_hash,auth_account_status,"email_verification_status",updated_at) VALUES
	 (1,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (2,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (3,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (4,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (5,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (6,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (7,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (8,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (9,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439');
INSERT INTO public.groups (owner_customer_id, name, description,created_at,updated_at) VALUES
	 (1, 'Friends', 'Friendly chat room','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 'Music', 'Talk about music','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (3, 'TV', 'Talk about tv','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (4, 'Cooking', 'Talk about cook','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (5, 'Hunt', 'Talk about hunting','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (6, 'Off-topic', 'Talk about everything else','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (7, 'Religion', 'Talk about religion','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (8, 'Programming', 'Programming talk','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (9, 'Movies', 'Movies talk','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237');
INSERT INTO public.group_members (group_id, member_customer_id, member_role, created_at,updated_at) VALUES
	 (1, 1, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 2, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (3, 3, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (4, 4, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (5, 5, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (6, 6, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (7, 7, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (8, 8, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (9, 9, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237');
INSERT INTO public.customer_contacts (customer_id,contact_customer_id,created_at,updated_at) VALUES
	 (1, 2,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 3,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 4,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 5,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 6,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 7,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 8,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 9,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 1,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 2,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 3,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237');

INSERT INTO public.customers (email,"role",created_at,updated_at) VALUES
	 ('admin@demo.com','ADMIN'::public."customer_role_type",'2025-04-17 02:07:41.382291','2025-04-17 02:07:41.382291'),
	 ('alice@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616'),
	 ('david@demo.com','CUSTOMER'::public."customer_role_type",'2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616');
INSERT INTO public.customer_profiles (customer_id,first_name,last_name,phone,birthdate,gender,avatar_filename,updated_at) VALUES
	 (1,'Damian','J.','701 444 113','1987-06-02','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:47:10.771533'),
	 (2,'Alice','White','701 444 113','1987-06-02','FEMALE'::public."customer_gender_type",NULL,'2025-04-28 01:47:10.771533'),
	 (3,'David','Brown','901 322 223','1993-07-04','MALE'::public."customer_gender_type",NULL,'2025-04-28 01:48:28.903419');
INSERT INTO public.customer_auth (customer_id,password_hash,auth_account_status,"email_verification_status",updated_at) VALUES
	 (1,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (2,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439'),
	 (3,'$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ENABLED'::public."auth_status_type",'NOT_VERIFIED'::public."email_verification_status_type",'2025-04-28 13:11:10.477439');
INSERT INTO public.groups (owner_customer_id, name, description,created_at,updated_at) VALUES
	 (1, 'Friends', 'Friendly chat room','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 'Music', 'Talk about music','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 'Movies', 'Movies talk','2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237');
INSERT INTO public.group_members (group_id, member_customer_id, member_role, created_at,updated_at) VALUES
	 (1, 1, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (2, 1, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (3, 2, 'OWNER'::public."group_member_role_type",'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237');
INSERT INTO public.customer_contacts (customer_id,contact_customer_id,created_at,updated_at) VALUES
	 (1, 2,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237'),
	 (1, 3,'2025-04-14 00:24:39.778237','2025-04-14 00:24:39.778237');

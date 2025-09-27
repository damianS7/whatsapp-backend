DROP SCHEMA IF EXISTS public CASCADE;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

COMMENT ON SCHEMA public IS 'standard public schema';


CREATE TYPE public."user_role_type" AS ENUM (
	'USER',
	'ADMIN'
);

CREATE CAST (varchar as user_role_type) WITH INOUT AS IMPLICIT;

CREATE TYPE public."account_status_type" AS ENUM (
	'PENDING_VERIFICATION',
	'SUSPENDED',
	'VERIFIED'
);

CREATE CAST (varchar as account_status_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.users (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_name varchar(20) NOT NULL,
   	first_name varchar(20) NOT NULL,
   	last_name varchar(40) NOT NULL,
   	phone varchar(14) NOT NULL,
   	birthdate date NOT NULL,
   	gender public."user_gender_type" NOT NULL,
   	image_filename varchar(100) NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TYPE public."user_gender_type" AS ENUM (
	'MALE',
	'FEMALE'
);

CREATE CAST (varchar as user_gender_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_accounts (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	email varchar(80) NOT NULL,
    password_hash varchar(60) NOT NULL,
    account_status public."account_status_type" DEFAULT 'PENDING_VERIFICATION'::account_status_type NOT NULL,
    "role" public."user_role_type" DEFAULT 'USER'::user_role_type NOT NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT user_accounts_email_key UNIQUE (email),
	CONSTRAINT user_accounts_pkey PRIMARY KEY (id),
	CONSTRAINT user_accounts_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TYPE public."account_token_type" AS ENUM (
	'ACCOUNT_VERIFICATION',
	'RESET_PASSWORD'
);

CREATE CAST (varchar as account_token_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_account_tokens (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_account_id int4 NOT NULL,
	token varchar(100) NOT NULL,
	used BOOLEAN DEFAULT FALSE,
	type public."account_token_type" NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	expires_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT account_token_pkey PRIMARY KEY (id),
	CONSTRAINT account_token_user_id_fkey FOREIGN KEY (user_account_id) REFERENCES public.user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE public.user_contacts (
    id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id int4 NOT NULL,
    contact_user_id int4 NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT user_contacts_id_pkey PRIMARY KEY (id),
    CONSTRAINT unique_user_follower UNIQUE (user_id, contact_user_id),
    CONSTRAINT user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT contact_user_id_fkey FOREIGN KEY (contact_user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TYPE public."notification_type" AS ENUM (
	'MESSAGE'
);

CREATE CAST (varchar as notification_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_notifications (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	type public."notification_type" NOT NULL,
	message varchar(255) NOT NULL,
	metadata jsonb NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT user_notifications_pkey PRIMARY KEY (id),
    CONSTRAINT user_notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TABLE public.user_settings (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	setting_key varchar(255) NOT NULL,
    setting_value varchar(255) NOT NULL,
	CONSTRAINT settings_pkey PRIMARY KEY (id),
    CONSTRAINT settings_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_setting UNIQUE (user_id, setting_key)
);

CREATE TABLE public.groups (
    id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    owner_user_id int4 NOT NULL,
    name varchar(30) NOT NULL,
    description varchar(220) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT group_name_unique UNIQUE (name),
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT owner_user_id_fkey FOREIGN KEY (owner_user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TYPE public."group_member_role_type" AS ENUM (
	'OWNER',
	'ADMIN',
	'MEMBER'
);

CREATE CAST (varchar as group_member_role_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.group_members (
    id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    group_id int4 NOT NULL,
    member_user_id int4 NOT NULL,
    member_role public."group_member_role_type" DEFAULT 'MEMBER'::group_member_role_type NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT group_member_unique UNIQUE (group_id, member_user_id),
    CONSTRAINT group_members_pkey PRIMARY KEY (id),
    CONSTRAINT group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(id) ON DELETE CASCADE,
    CONSTRAINT member_user_id_fkey FOREIGN KEY (member_user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

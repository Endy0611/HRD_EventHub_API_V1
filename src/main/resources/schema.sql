-- ============================================================
-- ALUMNI MANAGEMENT SYSTEM — FULL DATABASE SCHEMA
-- Updated with Bakong KHQR Payment Support
-- ============================================================

-- ============================================================
-- ENABLE EXTENSION
-- ============================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- ENUMS
-- ============================================================

CREATE TYPE post_type AS ENUM (
    'announcement',
    'event'
    );

CREATE TYPE file_type AS ENUM (
    'image',
    'video',
    'document'
    );

CREATE TYPE reaction_type AS ENUM (
    'like',
    'please',
    'too_hard',
    'great',
    'thank_you'
    );

CREATE TYPE current_status_type AS ENUM (
    'studying',
    'working',
    'job_seeking'
    );

CREATE TYPE payment_type AS ENUM (
    'event',
    'donation'
    );

CREATE TYPE transaction_status AS ENUM (
    'pending',
    'success',
    'failed'
    );

CREATE TYPE refund_status AS ENUM (
    'pending',
    'paid',
    'failed'
    );

CREATE TYPE payout_status AS ENUM (
    'pending',
    'paid',
    'failed'
    );

CREATE TYPE poll_type AS ENUM (
    'text',
    'date'
    );

CREATE TYPE show_result_type AS ENUM (
    'always',
    'after_vote',
    'after_close'
    );

-- ============================================================
-- AREA 1: AUTH & USERS
-- ============================================================

-- TABLE: app_users
CREATE TABLE app_users (
                           app_user_id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                           username            VARCHAR(50) NOT NULL UNIQUE,
                           first_name          VARCHAR(50) NOT NULL,
                           last_name           VARCHAR(50) NOT NULL,
                           email               VARCHAR(50) NOT NULL UNIQUE,
                           password            TEXT        NOT NULL,
                           date_of_birth       DATE,
                           phone_number        VARCHAR(50),
                           is_active           BOOLEAN     NOT NULL DEFAULT true,
                           telegram_subscribed BOOLEAN     NOT NULL DEFAULT false,
                           created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
                           updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- TABLE: roles
CREATE TABLE roles (
                       role_id   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

-- TABLE: app_user_roles
CREATE TABLE app_user_roles (
                                app_user_id UUID NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                                role_id     UUID NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
                                PRIMARY KEY (app_user_id, role_id)
);

-- TABLE: profile
CREATE TABLE profile (
                         profile_id           UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
                         bio                  TEXT,
                         profile_picture      VARCHAR(255),
                         university           VARCHAR(100),
                         major                VARCHAR(50),
                         skills               TEXT,
                         cv_resume_url        VARCHAR(255),
                         social_account       JSONB,
                         profile_sharing_mode BOOLEAN             NOT NULL DEFAULT false,
                         current_status       current_status_type,
                         preferences          JSONB               NOT NULL DEFAULT '{
                           "show_bio": true,
                           "show_birthdate": true,
                           "show_email": true,
                           "show_social": true,
                           "show_cv": true,
                           "show_skills": true,
                           "show_activity": true
                         }'::jsonb,
                         generation_id        UUID,
                         app_user_id          UUID                NOT NULL UNIQUE REFERENCES app_users(app_user_id) ON DELETE CASCADE
);

-- ============================================================
-- AREA 2: WORKSPACE
-- ============================================================

-- TABLE: generations
CREATE TABLE generations (
                             generation_id UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                             name          VARCHAR(50) NOT NULL,
                             year          VARCHAR(50) NOT NULL,
                             is_current    BOOLEAN     NOT NULL DEFAULT false,
                             created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                             app_user_id   UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT
);

-- Add FK from profile to generations
ALTER TABLE profile
    ADD CONSTRAINT fk_profile_generation
        FOREIGN KEY (generation_id) REFERENCES generations(generation_id) ON DELETE SET NULL;

-- TABLE: workspace
CREATE TABLE workspace (
                           workspace_id  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                           name          VARCHAR(100) NOT NULL,
                           description   VARCHAR(255),
                           is_public     BOOLEAN      NOT NULL DEFAULT false,
                           created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
                           created_by    UUID         NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,
                           generation_id UUID         REFERENCES generations(generation_id) ON DELETE SET NULL
);

-- TABLE: app_user_workspace
CREATE TABLE app_user_workspace (
                                    uw_id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                    join_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                    app_user_id  UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                                    workspace_id UUID        NOT NULL REFERENCES workspace(workspace_id) ON DELETE CASCADE,
                                    UNIQUE (app_user_id, workspace_id)
);

-- TABLE: invitations
CREATE TABLE invitations (
                             invitation_id UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                             token         VARCHAR(100) NOT NULL UNIQUE,
                             roles         VARCHAR(50),
                             created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
                             expired_at    TIMESTAMPTZ  NOT NULL,
                             generation_id UUID         REFERENCES generations(generation_id) ON DELETE SET NULL,
                             app_user_id   UUID         NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,
                             workspace_id  UUID         NOT NULL REFERENCES workspace(workspace_id) ON DELETE CASCADE
);

-- ============================================================
-- AREA 3: POSTS
-- ============================================================

-- TABLE: posts
CREATE TABLE posts (
                       post_id      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                       type         post_type    NOT NULL,
                       category     VARCHAR(100),
                       title        VARCHAR(100) NOT NULL,
                       content      TEXT,
                       is_published BOOLEAN      NOT NULL DEFAULT false,
                       schedule_at  TIMESTAMPTZ,
                       status       VARCHAR(50),
                       is_pinned    BOOLEAN      NOT NULL DEFAULT false,
                       expires_at   TIMESTAMPTZ,
                       event_date   TIMESTAMPTZ,
                       deleted_at   TIMESTAMPTZ,
                       created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       workspace_id UUID         NOT NULL REFERENCES workspace(workspace_id) ON DELETE CASCADE,
                       app_user_id  UUID         NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT
);

-- TABLE: post_workspace
CREATE TABLE post_workspace (
                                pw_id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                                post_id      UUID        NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
                                workspace_id UUID        NOT NULL REFERENCES workspace(workspace_id) ON DELETE CASCADE,
                                UNIQUE (post_id, workspace_id)
);

-- TABLE: post_participants
CREATE TABLE post_participants (
                                   participant_id UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                   register_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                                   post_id        UUID        NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
                                   app_user_id    UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                                   UNIQUE (post_id, app_user_id)
);

-- TABLE: post_media
CREATE TABLE post_media (
                            media_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                            file_name  VARCHAR(100) NOT NULL,
                            file_path  VARCHAR(100) NOT NULL,
                            file_type  file_type    NOT NULL,
                            created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            post_id    UUID         NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE
);

-- TABLE: location
CREATE TABLE location (
                          location_id    UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
                          google_map_url VARCHAR(255),
                          latitude       DOUBLE PRECISION,
                          longitude      DOUBLE PRECISION,
                          post_id        UUID             NOT NULL UNIQUE REFERENCES posts(post_id) ON DELETE CASCADE
);

-- ============================================================
-- AREA 4: ENGAGEMENT
-- ============================================================

-- TABLE: comments
CREATE TABLE comments (
                          comment_id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                          comment           TEXT        NOT NULL,
                          created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                          post_id           UUID        NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
                          app_user_id       UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                          mentioned_user_id UUID        REFERENCES app_users(app_user_id) ON DELETE SET NULL,  -- nullable: not all comments mention someone
                          parent_comment_id UUID        REFERENCES comments(comment_id) ON DELETE CASCADE
);

-- TABLE: reactions
CREATE TABLE reactions (
                           reaction_id UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                           type        reaction_type NOT NULL,
                           created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
                           post_id     UUID          NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
                           app_user_id UUID          NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                           UNIQUE (post_id, app_user_id)
);

-- TABLE: endorsements
CREATE TABLE endorsements (
                              endorsement_id UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                              skill          VARCHAR(50) NOT NULL,
                              comment        TEXT,
                              created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                              alumni_id      UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                              student_id     UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                              post_id        UUID        REFERENCES posts(post_id) ON DELETE SET NULL,
                              UNIQUE (alumni_id, student_id, skill)
);

-- ============================================================
-- AREA 5: PAYMENT
-- ============================================================

-- TABLE: payment_accounts
--   Stores both admin's receiving account & organizer's payout account
CREATE TABLE payment_accounts (
                                  payment_acc_id  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                                  account_name    VARCHAR(255) NOT NULL,
                                  account_number  VARCHAR(100) NOT NULL,
                                  bank_name       VARCHAR(255) NOT NULL,
                                  is_default      BOOLEAN      NOT NULL DEFAULT false,
                                  created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                  updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                  app_user_id     UUID         NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE
);

-- TABLE: post_payment
--   Admin's receiving account attached to an event post
--   payment_acc_id → admin account that collects from users
CREATE TABLE post_payment (
                              post_payment_id UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
                              payment_type    payment_type   NOT NULL,        -- 'event' | 'donation'
                              payment_amount  DECIMAL(10, 2),                 -- ticket price (null = free event)
                              donation_goal   DECIMAL(10, 2),                 -- only for donation type
                              amount_raised   DECIMAL(10, 2) NOT NULL DEFAULT 0,
                              created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
                              updated_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
                              post_id         UUID           NOT NULL UNIQUE REFERENCES posts(post_id) ON DELETE CASCADE,
                              payment_acc_id  UUID           NOT NULL REFERENCES payment_accounts(payment_acc_id) ON DELETE RESTRICT
);

-- TABLE: transactions
--   User pays admin via Bakong KHQR when registering for event
--   from_account_id is saved for use in refunds
CREATE TABLE transactions (
                              transaction_id      UUID               PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Core payment
                              amount              DECIMAL(10, 2)     NOT NULL,
                              currency            VARCHAR(3)         NOT NULL DEFAULT 'USD',
                              status              transaction_status NOT NULL DEFAULT 'pending',
                              is_anonymous        BOOLEAN            NOT NULL DEFAULT false,
                              paid                BOOLEAN            NOT NULL DEFAULT false,
                              paid_at             TIMESTAMPTZ,

    -- Bakong KHQR fields
                              qr_code             TEXT,                        -- KHQR string shown to user
                              qr_md5              VARCHAR(32),                 -- MD5 used for polling payment status
                              qr_expiration       TIMESTAMPTZ,                 -- when QR code expires
                              bakong_hash         VARCHAR(255),                -- returned by Bakong after success
                              bank_transaction_id VARCHAR(50),                 -- Bakong's own transaction reference

    -- Sender info (filled from Bakong callback — used later for refunds)
                              from_account_id     VARCHAR(100),                -- user's bank account number
                              to_account_id       VARCHAR(100),                -- admin's bank account number

    -- Timestamps
                              created_at          TIMESTAMPTZ        NOT NULL DEFAULT now(),
                              updated_at          TIMESTAMPTZ        NOT NULL DEFAULT now(),

    -- Relations
                              app_user_id         UUID               NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,
                              post_id             UUID               NOT NULL REFERENCES posts(post_id) ON DELETE RESTRICT
);

-- TABLE: post_refunds
--   Admin manually refunds user when organizer cancels event
--   to_account_id is copied from transactions.from_account_id on creation
CREATE TABLE post_refunds (
                              refund_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                              amount          DECIMAL(10,2) NOT NULL,
                              status          refund_status NOT NULL DEFAULT 'pending',
                              remark          TEXT,

    -- Refund destination (copied from transaction.from_account_id)
                              to_account_id   VARCHAR(100)  NOT NULL,          -- user's bank account number
                              to_account_name VARCHAR(255),                    -- user's account name
                              to_bank_name    VARCHAR(255),                    -- user's bank name

                              refunded_at     TIMESTAMPTZ,                     -- when admin actually transferred the money
                              created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
                              updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),

    -- Relations
                              transaction_id  UUID          NOT NULL UNIQUE REFERENCES transactions(transaction_id) ON DELETE RESTRICT,
                              post_id         UUID          NOT NULL REFERENCES posts(post_id) ON DELETE RESTRICT,
                              app_user_id     UUID          NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,  -- user who gets refund
                              processed_by    UUID          NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT   -- admin who processed it
);

-- TABLE: post_payouts
--   Admin pays organizer after event is finished (one payout per post)
CREATE TABLE post_payouts (
                              payout_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                              amount          DECIMAL(10,2) NOT NULL,
                              status          payout_status NOT NULL DEFAULT 'pending',
                              remark          TEXT,
                              transferred_at  TIMESTAMPTZ,                     -- when admin actually sent the money
                              created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
                              updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),

    -- Relations
                              post_id         UUID          NOT NULL UNIQUE REFERENCES posts(post_id) ON DELETE RESTRICT,
                              organizer_id    UUID          NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,  -- who receives payout
                              processed_by    UUID          NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,  -- admin who processed it
                              payment_acc_id  UUID          NOT NULL REFERENCES payment_accounts(payment_acc_id) ON DELETE RESTRICT  -- organizer's bank account
);

-- ============================================================
-- AREA 6: POLL
-- ============================================================

-- TABLE: polls
CREATE TABLE polls (
                       poll_id           UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
                       title             VARCHAR(255)     NOT NULL,
                       description       TEXT,
                       type              poll_type        NOT NULL,
                       options           JSONB            NOT NULL DEFAULT '[]'::jsonb,
                       closing_time      TIMESTAMPTZ,
                       multi_select      BOOLEAN          NOT NULL DEFAULT false,
                       anonymous_voting  BOOLEAN          NOT NULL DEFAULT false,
                       allow_add_options BOOLEAN          NOT NULL DEFAULT false,
                       hidden_poll       BOOLEAN          NOT NULL DEFAULT false,
                       show_result       show_result_type NOT NULL,
                       created_at        TIMESTAMPTZ      NOT NULL DEFAULT now(),
                       updated_at        TIMESTAMPTZ      NOT NULL DEFAULT now(),
                       workspace_id      UUID             NOT NULL REFERENCES workspace(workspace_id) ON DELETE CASCADE,
                       app_user_id       UUID             NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT
);

-- TABLE: poll_votes
CREATE TABLE poll_votes (
                            vote_id     UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                            created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                            option_id   UUID        NOT NULL,
                            poll_id     UUID        NOT NULL REFERENCES polls(poll_id) ON DELETE CASCADE,
                            app_user_id UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE,
                            UNIQUE (poll_id, option_id, app_user_id)
);

-- ============================================================
-- AREA 7: SURVEY
-- ============================================================

-- TABLE: surveys
CREATE TABLE surveys (
                         survey_id    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                         title        VARCHAR(255) NOT NULL,
                         description  TEXT,
                         content      JSONB        NOT NULL,
                         is_active    BOOLEAN      NOT NULL DEFAULT true,
                         closing_time TIMESTAMPTZ,
                         created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         app_user_id  UUID         NOT NULL REFERENCES app_users(app_user_id) ON DELETE RESTRICT,
                         workspace_id UUID         NOT NULL REFERENCES workspace(workspace_id) ON DELETE CASCADE
);

-- TABLE: survey_responses
CREATE TABLE survey_responses (
                                  response_id  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                  content      JSONB       NOT NULL,
                                  submitted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                  survey_id    UUID        NOT NULL REFERENCES surveys(survey_id) ON DELETE CASCADE,
                                  app_user_id  UUID        NOT NULL REFERENCES app_users(app_user_id) ON DELETE CASCADE
);

-- ============================================================
-- SEED: DEFAULT ROLES
-- ============================================================
INSERT INTO roles (role_name) VALUES
                                  ('admin'),
                                  ('organizer'),
                                  ('alumni'),
                                  ('student');

ALTER TABLE app_users
    ADD COLUMN is_verified BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE generations ADD COLUMN updated_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE workspace ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
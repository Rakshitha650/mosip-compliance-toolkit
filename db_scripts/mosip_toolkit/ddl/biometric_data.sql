-- This table have all the meta data of biometrics file

CREATE TABLE toolkit.biometric_data
(
    id character varying(36) NOT NULL,
    name character varying(64) NOT NULL,
    type character varying(36) NOT NULL,
    purpose character varying(36) NOT NULL,
    partner_id character varying(36) NOT NULL,
    file_id character varying(256) NOT NULL,
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp without time zone NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp without time zone,
    is_deleted boolean,
    del_dtimes timestamp without time zone,
    CONSTRAINT biometric_dataid_pk PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_biometric_data_id ON toolkit.biometric_data USING btree (id);
CREATE INDEX IF NOT EXISTS idx_biometric_data_partner_id ON toolkit.biometric_data USING btree (partner_id);
COMMENT ON TABLE toolkit.biometric_data IS 'This table have all the meta data of biometrics file added by the user.';
COMMENT ON COLUMN toolkit.biometric_data.id IS 'ID: Unique Id generated for biometric meta data.';
COMMENT ON COLUMN toolkit.biometric_data.name IS 'Name: name of the biometric meta data.';
COMMENT ON COLUMN toolkit.biometric_data.type IS 'Type: typeof project SDK or ABIS.';
COMMENT ON COLUMN toolkit.biometric_data.purpose IS 'Purpose: the purpose of the biometric data.';
COMMENT ON COLUMN toolkit.biometric_data.partner_id IS 'Partner Id: partner id who has created this project.';
COMMENT ON COLUMN toolkit.biometric_data.cr_by IS 'Created By : ID or name of the user who create / insert record.';
COMMENT ON COLUMN toolkit.biometric_data.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
COMMENT ON COLUMN toolkit.biometric_data.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
COMMENT ON COLUMN toolkit.biometric_data.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
COMMENT ON COLUMN toolkit.biometric_data.is_deleted IS 'is Deleted :flag to store soft delete status';
COMMENT ON COLUMN toolkit.biometric_data.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when record is deleted.';
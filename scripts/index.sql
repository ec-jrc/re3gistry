CREATE INDEX idx_languagecode_masterlanguage
    ON public.reg_languagecode USING btree
    (masterlanguage ASC NULLS LAST)
;

CREATE INDEX idx_relationpredicate_uuid
    ON public.reg_relationpredicate USING btree
    (uuid ASC NULLS LAST)
;

CREATE INDEX idx_relation_item_subject_and_relationpredicate
    ON public.reg_relation USING btree
    (reg_item_subject ASC NULLS LAST, reg_relationpredicate ASC NULLS LAST)
;

CREATE INDEX idx_relation_uuid
    ON public.reg_relation USING btree
    (uuid ASC NULLS LAST)
;

CREATE INDEX idx_localization_field_and_item_and_languagecode
    ON public.reg_localization USING btree
    (reg_field ASC NULLS LAST, reg_item ASC NULLS LAST, reg_languagecode ASC NULLS LAST)
;

CREATE INDEX idx_relation_item_object_and_relationpredicate
    ON public.reg_relation USING btree
    (reg_item_object ASC NULLS LAST, reg_relationpredicate ASC NULLS LAST)
;

CREATE INDEX idx_itemhistory_item_reference
    ON public.reg_itemhistory USING btree
    (reg_item_reference ASC NULLS LAST)
;

CREATE INDEX idx_field_localid
    ON public.reg_field USING btree
    (localid ASC NULLS LAST)
;

CREATE INDEX idx_fieldmapping_itemclass
    ON public.reg_fieldmapping USING btree
    (reg_itemclass ASC NULLS LAST)
;

CREATE INDEX idx_group_localid
    ON public.reg_group USING btree
    (localid ASC NULLS LAST)
;

CREATE INDEX idx_fieldmapping_field_and_itemclass
    ON public.reg_fieldmapping USING btree
    (reg_field ASC NULLS LAST, reg_itemclass ASC NULLS LAST)
;

CREATE INDEX idx_item_localid_and_itemclass
    ON public.reg_item USING btree
    (localid ASC NULLS LAST, reg_itemclass ASC NULLS LAST)
;


CREATE INDEX idx_itemclass_localid
    ON public.reg_itemclass USING btree
    (localid ASC NULLS LAST)
;

CREATE INDEX idx_fieldtype_uuid
    ON public.reg_fieldtype USING btree
    (uuid ASC NULLS LAST)
;

CREATE INDEX idx_itemclass_itemclass_parent
    ON public.reg_itemclass USING btree
    (reg_itemclass_parent ASC NULLS LAST)
;

CREATE INDEX idx_statuslocalization_status_and_languagecode
    ON public.reg_statuslocalization USING btree
    (reg_status ASC NULLS LAST, reg_languagecode ASC NULLS LAST)
;

CREATE INDEX idx_localization_field_and_item
    ON public.reg_localization USING btree
    (reg_field ASC NULLS LAST, reg_item ASC NULLS LAST)
;

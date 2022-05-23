
INSERT INTO public.reg_field VALUES ('c6c8c937f36cab208cca254e379e07ce', 'label', '1', true, NULL, NULL, '1', '2022-05-17 11:52:32.782', NULL);
INSERT INTO public.reg_field VALUES ('b6fbf20d3264600d468ceed12947d6b1', 'contentsummary', '11', false, NULL, NULL, '1', '2022-05-17 11:52:32.857', NULL);
INSERT INTO public.reg_field VALUES ('3082cf25e86792cb06ea57bc497d385f', 'registryManager', '12', false, NULL, '1', '1', '2022-05-17 11:52:32.903', NULL);
INSERT INTO public.reg_field VALUES ('f843fae58bd04c0bc48f30b93c7fb052', 'definition', '11', false, NULL, NULL, '1', '2022-05-17 11:52:32.954', NULL);
INSERT INTO public.reg_field VALUES ('21e2c582d9480c09c100879ddfcb33a1', 'status', '13', false, NULL, NULL, '1', '2022-05-17 11:52:32.987', NULL);
INSERT INTO public.reg_field VALUES ('4fbbd7fa3808245a60a44fa84b0ba34a', 'registerManager', '12', false, NULL, '2', '1', '2022-05-17 11:52:33.006', NULL);
INSERT INTO public.reg_field VALUES ('20003018745a0e304abef9f90b41158e', 'registerOwner', '12', false, NULL, '3', '1', '2022-05-17 11:52:33.027', NULL);
INSERT INTO public.reg_field VALUES ('a01bd4fa426d4b64a57ed2f6eebff94f', 'controlBody', '12', false, NULL, '4', '1', '2022-05-17 11:52:33.046', NULL);
INSERT INTO public.reg_field VALUES ('be53cbf85a488ae0fe958951df7f4fd9', 'submittingOrganization', '12', false, NULL, '5', '1', '2022-05-17 11:52:33.063', NULL);

INSERT INTO public.reg_itemclass VALUES ('8f81ea04e282e23fa14493fc696869aa', 'registry', 'http://host.docker.internal', false, true, NULL, '1', '1', 0, '2022-05-17 11:52:32.709', NULL);
INSERT INTO public.reg_itemclass VALUES ('3ba7544f8544457bf95605437b21de33', 'examplecodelist', 'http://host.docker.internal/registry', false, true, '8f81ea04e282e23fa14493fc696869aa', '2', '1', 1, '2022-05-17 11:53:14.524', NULL);
INSERT INTO public.reg_itemclass VALUES ('9b809cd520df46f068707dff78bdf83f', 'examplecodelist-item', NULL, false, true, '3ba7544f8544457bf95605437b21de33', '3', '1', 2, '2022-05-17 11:53:14.657', NULL);

INSERT INTO public.reg_fieldmapping VALUES ('68f9d7f133c8e7e11baf3c9fa58cc936', 'c6c8c937f36cab208cca254e379e07ce', '8f81ea04e282e23fa14493fc696869aa', 0, true, '1', true, false, false, '2022-05-17 11:52:32.84', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('f9320dc3ddbc81bc6ffb55e9ed143974', 'b6fbf20d3264600d468ceed12947d6b1', '8f81ea04e282e23fa14493fc696869aa', 1, true, '1', false, false, false, '2022-05-17 11:52:32.874', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('a8c35fcc16e7ddfd10ef8755ea5c112c', '3082cf25e86792cb06ea57bc497d385f', '8f81ea04e282e23fa14493fc696869aa', 2, true, '1', true, false, false, '2022-05-17 11:52:32.919', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('5481fd357e87c889f4e032787f7344d0', '21e2c582d9480c09c100879ddfcb33a1', '3ba7544f8544457bf95605437b21de33', 2, true, '1', true, false, false, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('2a1c47250d6f58102bb98f5d2d711fbc', 'c6c8c937f36cab208cca254e379e07ce', '3ba7544f8544457bf95605437b21de33', 0, true, '1', true, false, false, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('27a6291a4289dbd11465e654e4e8b30e', 'a01bd4fa426d4b64a57ed2f6eebff94f', '3ba7544f8544457bf95605437b21de33', 5, false, '1', true, false, false, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('eda6b08c8e75bb9515ba07b130ae0593', '20003018745a0e304abef9f90b41158e', '3ba7544f8544457bf95605437b21de33', 4, false, '1', true, false, false, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('350d36e24ddb9bcbbaac4f1aef93b754', '4fbbd7fa3808245a60a44fa84b0ba34a', '3ba7544f8544457bf95605437b21de33', 3, false, '1', true, false, false, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('b34bfa6ad3947a923ca928fb1bab17d6', 'b6fbf20d3264600d468ceed12947d6b1', '3ba7544f8544457bf95605437b21de33', 1, true, '1', true, false, false, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('d856ad966ea95bd639d80dcb8e38e427', 'be53cbf85a488ae0fe958951df7f4fd9', '3ba7544f8544457bf95605437b21de33', 6, false, '1', true, false, true, '2022-05-17 11:53:14.605', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('b32bf03fe9a3e07a085a222f9b52755e', '21e2c582d9480c09c100879ddfcb33a1', '9b809cd520df46f068707dff78bdf83f', 2, true, '1', true, false, false, '2022-05-17 11:53:14.711', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('508cd613a36897cbbc2f5ca6ead076b0', 'c6c8c937f36cab208cca254e379e07ce', '9b809cd520df46f068707dff78bdf83f', 0, true, '1', true, false, false, '2022-05-17 11:53:14.711', NULL, false);
INSERT INTO public.reg_fieldmapping VALUES ('441c4af028d9011140a1e236780e48d6', 'f843fae58bd04c0bc48f30b93c7fb052', '9b809cd520df46f068707dff78bdf83f', 1, true, '1', true, false, false, '2022-05-17 11:53:14.711', NULL, false);


INSERT INTO public.reg_group VALUES ('6013f1ddc7680378215f8cf733f10034', 'registryManager', 'Registry Manager', NULL, NULL, '2022-05-17 11:52:01.517', NULL);
INSERT INTO public.reg_group VALUES ('e882caa53ece274ea9e22b1f9050c9d8', 'registerManager', 'Register Manager', NULL, NULL, '2022-05-17 11:52:01.611', NULL);
INSERT INTO public.reg_group VALUES ('53c9a3557f9090758c1170f9aec94450', 'registerOwner', 'Register Owner', NULL, NULL, '2022-05-17 11:52:01.644', NULL);
INSERT INTO public.reg_group VALUES ('d8d77d2743396b0e2551df23d11b6483', 'submittingOrganization', 'Register Submitter', NULL, NULL, '2022-05-17 11:52:01.694', NULL);
INSERT INTO public.reg_group VALUES ('0ca8ffbead7a6c465629a8dcfd1b523b', 'controlBody', 'Control Body', NULL, NULL, '2022-05-17 11:52:01.722', NULL);


INSERT INTO public.reg_user VALUES ('66bb284e6748abf283006a2c51c9b17d', 'name@example.com', 'name@example.com', 'ARgDpmMO8b//E0aw9FAZR03fvXmCOA8UOnuP1mv89yo=', 'H39UUfuRZ7zl66aKRFE6SQ==', 'name@example.com', true, '2022-05-17 11:52:01.41', NULL);



INSERT INTO public.reg_item VALUES ('27f3dbdc7dbc69e5c2b3aa3ef79dc491', 'registry', '8f81ea04e282e23fa14493fc696869aa', '1', NULL, false, 0, '2022-05-17 11:52:32.709', NULL, '66bb284e6748abf283006a2c51c9b17d', false);
INSERT INTO public.reg_action VALUES ('dd5cc39405a16b6803057af0618d1881', 'Action on Re3gistry2 Devel', '66bb284e6748abf283006a2c51c9b17d', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', NULL, '14', NULL, NULL, NULL, '66bb284e6748abf283006a2c51c9b17d', NULL, NULL, NULL, false, NULL, '2022-05-17 11:53:14.753', '2022-05-17 11:53:24.773551');
INSERT INTO public.reg_item VALUES ('0222a01e7049835641f62737a3437ea4', 'examplecodelist', '3ba7544f8544457bf95605437b21de33', '1', 'dd5cc39405a16b6803057af0618d1881', false, 0, '2022-05-17 11:53:24.806', NULL, '66bb284e6748abf283006a2c51c9b17d', true);
INSERT INTO public.reg_action VALUES ('5b0029a428928eff8f6ec3d5278b3829', 'Action on examplecodelist', '66bb284e6748abf283006a2c51c9b17d', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', '0222a01e7049835641f62737a3437ea4', '14', '66bb284e6748abf283006a2c51c9b17d', '66bb284e6748abf283006a2c51c9b17d', NULL, '66bb284e6748abf283006a2c51c9b17d', NULL, 'a', NULL, false, NULL, '2022-05-17 12:18:54.06', '2022-05-17 12:20:40.441891');
INSERT INTO public.reg_item VALUES ('cf79f270f8cf2c27ddc54551bc00b96e', 'item01', '9b809cd520df46f068707dff78bdf83f', '1', '5b0029a428928eff8f6ec3d5278b3829', false, 0, '2022-05-17 12:20:40.47', NULL, '66bb284e6748abf283006a2c51c9b17d', false);
INSERT INTO public.reg_action VALUES ('66a0f5e7c754ae2fb47f1d37cc7ed4b8', 'Action on examplecodelist', '66bb284e6748abf283006a2c51c9b17d', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', '0222a01e7049835641f62737a3437ea4', '5', NULL, NULL, NULL, NULL, NULL, NULL, NULL, false, NULL, '2022-05-17 12:21:23.333', NULL);

INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('67382c41bd73c319c21c9b8cb94baa90', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', '6013f1ddc7680378215f8cf733f10034', '1', '2022-05-17 11:52:32.942', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('3cc8b75277ec352b99b8e4d6335d2516', '0222a01e7049835641f62737a3437ea4', '53c9a3557f9090758c1170f9aec94450', '3', '2022-05-17 11:53:24.912', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('84c3b48b2e2075b6a017d76b5fc02ec9', '0222a01e7049835641f62737a3437ea4', 'e882caa53ece274ea9e22b1f9050c9d8', '2', '2022-05-17 11:53:24.932', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('58850b50ede39749ba08977e451b1665', '0222a01e7049835641f62737a3437ea4', '0ca8ffbead7a6c465629a8dcfd1b523b', '4', '2022-05-17 11:53:24.941', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('8ad2565c94312c2e919007d9a129f4ae', '0222a01e7049835641f62737a3437ea4', '6013f1ddc7680378215f8cf733f10034', '5', '2022-05-17 11:53:24.954', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('f3012ef2c3bfcf7cda4914c705948d55', '0222a01e7049835641f62737a3437ea4', 'e882caa53ece274ea9e22b1f9050c9d8', '5', '2022-05-17 11:53:24.965', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('f7796c4e2eb423544e5c57038c4564ea', '0222a01e7049835641f62737a3437ea4', '53c9a3557f9090758c1170f9aec94450', '5', '2022-05-17 11:53:24.971', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('7daf8f2e17ead0d0c62bf1c3ea81c982', '0222a01e7049835641f62737a3437ea4', 'd8d77d2743396b0e2551df23d11b6483', '5', '2022-05-17 11:53:24.987', NULL);
INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('0d989872a678fc26225d22159d0db7e7', '0222a01e7049835641f62737a3437ea4', '0ca8ffbead7a6c465629a8dcfd1b523b', '5', '2022-05-17 11:53:24.999', NULL);




INSERT INTO public.reg_itemproposed VALUES ('74c3fffb6e6c201bcf4d5ab2343dcb91', NULL, false, 'item02', '5', '9b809cd520df46f068707dff78bdf83f', '66a0f5e7c754ae2fb47f1d37cc7ed4b8', '2022-05-17 12:21:23.303', NULL, '66bb284e6748abf283006a2c51c9b17d', false);


INSERT INTO public.reg_localization VALUES ('d70d2a3e476c65892cdba9afd55a1a59', 'en', NULL, 'c6c8c937f36cab208cca254e379e07ce', 0, 'label', NULL, NULL, NULL, '2022-05-17 11:52:32.82', NULL);
INSERT INTO public.reg_localization VALUES ('e9bf94186da6867afb86b60129caa7dd', 'en', NULL, 'b6fbf20d3264600d468ceed12947d6b1', 0, 'contentsummary', NULL, NULL, NULL, '2022-05-17 11:52:32.865', NULL);
INSERT INTO public.reg_localization VALUES ('1dcd333bba98c72b14a05408ff7ed962', 'en', NULL, '3082cf25e86792cb06ea57bc497d385f', 0, 'registryManager', NULL, NULL, NULL, '2022-05-17 11:52:32.908', NULL);
INSERT INTO public.reg_localization VALUES ('f8a82b07b361cbab48ee09a7b25ddab7', 'en', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', 'c6c8c937f36cab208cca254e379e07ce', 0, 'Re3gistry2 Devel', NULL, NULL, NULL, '2022-05-17 11:52:32.925', NULL);
INSERT INTO public.reg_localization VALUES ('f381ef94b6ff53e0caa1d822e40d0ae8', 'en', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', 'b6fbf20d3264600d468ceed12947d6b1', 0, 'Devel.', NULL, NULL, NULL, '2022-05-17 11:52:32.925', NULL);
INSERT INTO public.reg_localization VALUES ('9199c4f6925ac5711ae748d0ab0175b6', 'en', NULL, 'f843fae58bd04c0bc48f30b93c7fb052', 0, 'definition', NULL, NULL, NULL, '2022-05-17 11:52:32.975', NULL);
INSERT INTO public.reg_localization VALUES ('114df403b97fc66322e4d4634473a2af', 'en', NULL, '21e2c582d9480c09c100879ddfcb33a1', 0, 'status', NULL, NULL, NULL, '2022-05-17 11:52:32.999', NULL);
INSERT INTO public.reg_localization VALUES ('778f0904fe5612b52050d045121f8909', 'en', NULL, '4fbbd7fa3808245a60a44fa84b0ba34a', 0, 'registerManager', NULL, NULL, NULL, '2022-05-17 11:52:33.015', NULL);
INSERT INTO public.reg_localization VALUES ('3b97fca2afb74dcc4ca58898cdc45b44', 'en', NULL, '20003018745a0e304abef9f90b41158e', 0, 'registerOwner', NULL, NULL, NULL, '2022-05-17 11:52:33.038', NULL);
INSERT INTO public.reg_localization VALUES ('64f61b7d2f3e34c7385b95ca37cff2ac', 'en', NULL, 'a01bd4fa426d4b64a57ed2f6eebff94f', 0, 'controlBody', NULL, NULL, NULL, '2022-05-17 11:52:33.053', NULL);
INSERT INTO public.reg_localization VALUES ('5e32e0325de9cdf6dc3e75670ee103c8', 'en', NULL, 'be53cbf85a488ae0fe958951df7f4fd9', 0, 'submittingOrganization', NULL, NULL, NULL, '2022-05-17 11:52:33.069', NULL);
INSERT INTO public.reg_localization VALUES ('9c8cdf339c0fb21bc189e9e5f12aa614', 'en', '0222a01e7049835641f62737a3437ea4', 'c6c8c937f36cab208cca254e379e07ce', 0, 'examplecodelist', NULL, 'dd5cc39405a16b6803057af0618d1881', NULL, '2022-05-17 11:53:24.852', NULL);
INSERT INTO public.reg_localization VALUES ('fab794980d26df0f7543662a6efc01d6', 'en', '0222a01e7049835641f62737a3437ea4', 'b6fbf20d3264600d468ceed12947d6b1', 0, '<p>examplecodelist</p>', NULL, 'dd5cc39405a16b6803057af0618d1881', NULL, '2022-05-17 11:53:24.869', NULL);
INSERT INTO public.reg_localization VALUES ('cfe861baf368c09fa84b8250b1027073', 'en', 'cf79f270f8cf2c27ddc54551bc00b96e', 'c6c8c937f36cab208cca254e379e07ce', 0, 'VALID ITEM - Item01', NULL, '5b0029a428928eff8f6ec3d5278b3829', NULL, '2022-05-17 12:20:40.515', NULL);
INSERT INTO public.reg_localization VALUES ('f0373d38a01fb410bb59ff3600aac46a', 'en', 'cf79f270f8cf2c27ddc54551bc00b96e', 'f843fae58bd04c0bc48f30b93c7fb052', 0, '', NULL, '5b0029a428928eff8f6ec3d5278b3829', NULL, '2022-05-17 12:20:40.533', NULL);


INSERT INTO public.reg_localizationproposed VALUES ('a78e12a952c6bae16875b31c34c1add1', NULL, 'en', '74c3fffb6e6c201bcf4d5ab2343dcb91', 'c6c8c937f36cab208cca254e379e07ce', 0, 'DRAFT ITEM - Item02', NULL, '66a0f5e7c754ae2fb47f1d37cc7ed4b8', NULL, '2022-05-17 12:21:23.426', NULL);
INSERT INTO public.reg_localizationproposed VALUES ('0428267c2c874e275663f1d802fda73e', NULL, 'en', '74c3fffb6e6c201bcf4d5ab2343dcb91', 'f843fae58bd04c0bc48f30b93c7fb052', 0, '<p>aa</p>', NULL, '66a0f5e7c754ae2fb47f1d37cc7ed4b8', NULL, '2022-05-17 12:21:23.436', NULL);


INSERT INTO public.reg_relation VALUES ('e80051d360a6c34e5474d872357745f2', '0222a01e7049835641f62737a3437ea4', '1', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', '2022-05-17 11:53:24.819', NULL);
INSERT INTO public.reg_relation VALUES ('1058f2d2dbd758828b50dc4d20826655', 'cf79f270f8cf2c27ddc54551bc00b96e', '1', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', '2022-05-17 12:20:40.483', NULL);
INSERT INTO public.reg_relation VALUES ('aba70a65bef813f4d9c33df392796402', 'cf79f270f8cf2c27ddc54551bc00b96e', '2', '0222a01e7049835641f62737a3437ea4', '2022-05-17 12:20:40.483', NULL);


INSERT INTO public.reg_relationproposed VALUES ('a24cd8978ce1ea093b07906f0c10b29b', NULL, NULL, '74c3fffb6e6c201bcf4d5ab2343dcb91', '0222a01e7049835641f62737a3437ea4', NULL, '2', '2022-05-17 12:21:23.454', NULL);
INSERT INTO public.reg_relationproposed VALUES ('dde4183bc67dd65eda945055ddc345b3', NULL, NULL, '74c3fffb6e6c201bcf4d5ab2343dcb91', '27f3dbdc7dbc69e5c2b3aa3ef79dc491', NULL, '1', '2022-05-17 12:21:23.454', NULL);



--
-- TOC entry 3300 (class 0 OID 16548)
-- Dependencies: 222
-- Data for Name: reg_user_reg_group_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.reg_user_reg_group_mapping VALUES ('5a8e6a2d14aa953f9e022350e8edd8ab', '6013f1ddc7680378215f8cf733f10034', '66bb284e6748abf283006a2c51c9b17d', true, '2022-05-17 11:52:01.753', NULL);
INSERT INTO public.reg_user_reg_group_mapping VALUES ('78d9650345d75ad41c16f07ae7f3ee8b', 'e882caa53ece274ea9e22b1f9050c9d8', '66bb284e6748abf283006a2c51c9b17d', true, '2022-05-17 11:52:01.774', NULL);
INSERT INTO public.reg_user_reg_group_mapping VALUES ('4244e09c22537718d713608df269c558', '53c9a3557f9090758c1170f9aec94450', '66bb284e6748abf283006a2c51c9b17d', true, '2022-05-17 11:52:01.788', NULL);
INSERT INTO public.reg_user_reg_group_mapping VALUES ('3ae09e3fc3f3ff0ae0755fb246626e99', 'd8d77d2743396b0e2551df23d11b6483', '66bb284e6748abf283006a2c51c9b17d', true, '2022-05-17 11:52:01.803', NULL);
INSERT INTO public.reg_user_reg_group_mapping VALUES ('d1787d15f39f71021f250bed42aeb9c4', '0ca8ffbead7a6c465629a8dcfd1b523b', '66bb284e6748abf283006a2c51c9b17d', true, '2022-05-17 11:52:01.824', NULL);

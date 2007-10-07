-- This patch creates the "Business Check" Template.
--
-- It consists of:
-- * four assessment layers ("Bewertungsebenen")
-- * eight top-level criteria
-- * 24 second-level criteria (each top-level criterion has three subcriteria)

-- ===========================================================================

--- create template "Business Check"
INSERT INTO Topic VALUES ('tt-kompetenzsterntemplate', 1, 1, 't-businesscheck', 'Business Check');
INSERT INTO TopicProp VALUES ('t-businesscheck', 1, 'Name', 'Business Check');

-- ===========================================================================

--- create assessment layers
INSERT INTO Topic VALUES ('tt-bewertungsebene', 1, 1, 't-chancen',    'Chancen');
INSERT INTO Topic VALUES ('tt-bewertungsebene', 1, 1, 't-plaene',     'Pläne');
INSERT INTO Topic VALUES ('tt-bewertungsebene', 1, 1, 't-ablaeufe',   'Abläufe');
INSERT INTO Topic VALUES ('tt-bewertungsebene', 1, 1, 't-ergebnisse', 'Ergebnisse');
INSERT INTO TopicProp VALUES ('t-chancen',  1,   'Name', 'Chancen');
INSERT INTO TopicProp VALUES ('t-plaene', 1,     'Name', 'Pläne');
INSERT INTO TopicProp VALUES ('t-ablaeufe', 1,   'Name', 'Abläufe');
INSERT INTO TopicProp VALUES ('t-ergebnisse', 1, 'Name', 'Ergebnisse');

--- assign assessment layers to template "Business Check"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-650', '', 't-businesscheck', 1, 't-chancen', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-651', '', 't-businesscheck', 1, 't-plaene', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-652', '', 't-businesscheck', 1, 't-ablaeufe', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-653', '', 't-businesscheck', 1, 't-ergebnisse', 1);
INSERT INTO AssociationProp VALUES ('a-650', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-651', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-652', 1, 'Ordinal Number', '3');
INSERT INTO AssociationProp VALUES ('a-653', 1, 'Ordinal Number', '4');

-- ===========================================================================

--- create top-level criteria
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-ziele',      'Ziele');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-management', 'Management');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-betrieb',    'Betrieb');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-ertraege',   'Erträge');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-produkte',   'Produkte');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-vertrieb',   'Vertrieb');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-marketing',  'Marketing');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-markt',      'Markt');
INSERT INTO TopicProp VALUES ('t-ziele', 1,      'Name', 'Ziele');
INSERT INTO TopicProp VALUES ('t-management', 1, 'Name', 'Management');
INSERT INTO TopicProp VALUES ('t-betrieb', 1,    'Name', 'Betrieb');
INSERT INTO TopicProp VALUES ('t-ertraege', 1,   'Name', 'Erträge');
INSERT INTO TopicProp VALUES ('t-produkte', 1,   'Name', 'Produkte');
INSERT INTO TopicProp VALUES ('t-vertrieb', 1,   'Name', 'Vertrieb');
INSERT INTO TopicProp VALUES ('t-marketing', 1,  'Name', 'Marketing');
INSERT INTO TopicProp VALUES ('t-markt', 1,      'Name', 'Markt');
INSERT INTO TopicProp VALUES ('t-ziele', 1,      'Hilfe', 'Der Hilfe-Text für "Ziele" ...');
INSERT INTO TopicProp VALUES ('t-management', 1, 'Hilfe', 'Der Hilfe-Text für "Management" ...');
INSERT INTO TopicProp VALUES ('t-betrieb', 1,    'Hilfe', 'Der Hilfe-Text für "Betrieb" ...');
INSERT INTO TopicProp VALUES ('t-ertraege', 1,   'Hilfe', 'Der Hilfe-Text für "Erträge" ...');
INSERT INTO TopicProp VALUES ('t-produkte', 1,   'Hilfe', 'Der Hilfe-Text für "Produkte" ...');
INSERT INTO TopicProp VALUES ('t-vertrieb', 1,   'Hilfe', 'Der Hilfe-Text für "Vertrieb" ...');
INSERT INTO TopicProp VALUES ('t-marketing', 1,  'Hilfe', 'Der Hilfe-Text für "Marketing" ...');
INSERT INTO TopicProp VALUES ('t-markt', 1,      'Hilfe', 'Der Hilfe-Text für "Markt" ...');

--- assign top-level criteria to template "Business Check"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-642', '', 't-businesscheck', 1, 't-ziele', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-643', '', 't-businesscheck', 1, 't-management', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-644', '', 't-businesscheck', 1, 't-betrieb', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-645', '', 't-businesscheck', 1, 't-ertraege', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-649', '', 't-businesscheck', 1, 't-produkte', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-648', '', 't-businesscheck', 1, 't-vertrieb', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-647', '', 't-businesscheck', 1, 't-marketing', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-646', '', 't-businesscheck', 1, 't-markt', 1);

--- order top-level criteria
INSERT INTO AssociationProp VALUES ('a-642', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-643', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-644', 1, 'Ordinal Number', '3');
INSERT INTO AssociationProp VALUES ('a-645', 1, 'Ordinal Number', '4');
INSERT INTO AssociationProp VALUES ('a-649', 1, 'Ordinal Number', '5');
INSERT INTO AssociationProp VALUES ('a-648', 1, 'Ordinal Number', '6');
INSERT INTO AssociationProp VALUES ('a-647', 1, 'Ordinal Number', '7');
INSERT INTO AssociationProp VALUES ('a-646', 1, 'Ordinal Number', '8');

-- ===========================================================================

--- create second-level criteria
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-innovationspotenziale', 'Innovationspotenziale');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-unternehmenszukunft',   'Unternehmenszukunft');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-unternehmenspartner',   'Unternehmenspartner');
INSERT INTO TopicProp VALUES ('t-innovationspotenziale', 1, 'Name', 'Innovationspotenziale');
INSERT INTO TopicProp VALUES ('t-unternehmenszukunft', 1,   'Name', 'Unternehmenszukunft');
INSERT INTO TopicProp VALUES ('t-unternehmenspartner', 1,   'Name', 'Unternehmenspartner');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-wettbewerb-zugang', 'Wettbewerb, Zugang');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-marktpartner',      'Marktpartner');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-markterwartungen',  'Markterwartungen');
INSERT INTO TopicProp VALUES ('t-wettbewerb-zugang', 1, 'Name', 'Wettbewerb, Zugang');
INSERT INTO TopicProp VALUES ('t-marktpartner', 1,      'Name', 'Marktpartner');
INSERT INTO TopicProp VALUES ('t-markterwartungen', 1,  'Name', 'Markterwartungen');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-produktplanung',    'Produktplanung');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-geschaeftsleitung', 'Geschäftsleitung');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-techn-loesungen',   'Techn. Lösungen');
INSERT INTO TopicProp VALUES ('t-produktplanung', 1,    'Name', 'Produktplanung');
INSERT INTO TopicProp VALUES ('t-geschaeftsleitung', 1, 'Name', 'Geschäftsleitung');
INSERT INTO TopicProp VALUES ('t-techn-loesungen', 1,   'Name', 'Techn. Lösungen');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-kommunikation',       'Kommunikation');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-produktmarketing',    'Produktmarketing');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-preisattraktivitaet', 'Preisattraktivität');
INSERT INTO TopicProp VALUES ('t-kommunikation', 1,       'Name', 'Kommunikation');
INSERT INTO TopicProp VALUES ('t-produktmarketing', 1,    'Name', 'Produktmarketing');
INSERT INTO TopicProp VALUES ('t-preisattraktivitaet', 1, 'Name', 'Preisattraktivität');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-betriebsstruktur', 'Betriebsstruktur');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-betriebsqualitaet', 'Betriebsqualität');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-betriebsablaeufe', 'Betriebsabläufe');
INSERT INTO TopicProp VALUES ('t-betriebsstruktur', 1, 'Name', 'Betriebsstruktur');
INSERT INTO TopicProp VALUES ('t-betriebsqualitaet', 1, 'Name', 'Betriebsqualität');
INSERT INTO TopicProp VALUES ('t-betriebsablaeufe', 1, 'Name', 'Betriebsabläufe');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-vertriebsstruktur',  'Vertriebsstruktur');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-vertriebsqualitaet', 'Vertriebsqualität');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-vertriebsablaeufe',  'Vertriebsabläufe');
INSERT INTO TopicProp VALUES ('t-vertriebsstruktur', 1,  'Name', 'Vertriebsstruktur');
INSERT INTO TopicProp VALUES ('t-vertriebsqualitaet', 1, 'Name', 'Vertriebsqualität');
INSERT INTO TopicProp VALUES ('t-vertriebsablaeufe', 1,  'Name', 'Vertriebsabläufe');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-wirtschaftlichkeit', 'Wirtschaftlichkeit');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-ertragslage',        'Ertragslage');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-zielabweichungen',   'Zielabweichungen');
INSERT INTO TopicProp VALUES ('t-wirtschaftlichkeit', 1, 'Name', 'Wirtschaftlichkeit');
INSERT INTO TopicProp VALUES ('t-ertragslage', 1,        'Name', 'Ertragslage');
INSERT INTO TopicProp VALUES ('t-zielabweichungen', 1,   'Name', 'Zielabweichungen');

INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-produktleistungen', 'Produktleistungen');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-kundennutzen',      'Kundennutzen');
INSERT INTO Topic VALUES ('tt-kriterium', 1, 1, 't-marktabweichungen', 'Marktabweichungen');
INSERT INTO TopicProp VALUES ('t-produktleistungen', 1, 'Name', 'Produktleistungen');
INSERT INTO TopicProp VALUES ('t-kundennutzen',      1, 'Name', 'Kundennutzen');
INSERT INTO TopicProp VALUES ('t-marktabweichungen', 1, 'Name', 'Marktabweichungen');

--- assign second-level criteria to top-level criteria
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-732', '', 't-ziele', 1, 't-innovationspotenziale', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-733', '', 't-ziele', 1, 't-unternehmenszukunft', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-734', '', 't-ziele', 1, 't-unternehmenspartner', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-735', '', 't-markt', 1, 't-wettbewerb-zugang', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-736', '', 't-markt', 1, 't-marktpartner', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-737', '', 't-markt', 1, 't-markterwartungen', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-738', '', 't-management', 1, 't-produktplanung', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-739', '', 't-management', 1, 't-geschaeftsleitung', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-740', '', 't-management', 1, 't-techn-loesungen', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-741', '', 't-marketing', 1, 't-kommunikation', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-742', '', 't-marketing', 1, 't-produktmarketing', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-743', '', 't-marketing', 1, 't-preisattraktivitaet', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-744', '', 't-betrieb', 1, 't-betriebsstruktur', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-745', '', 't-betrieb', 1, 't-betriebsqualitaet', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-746', '', 't-betrieb', 1, 't-betriebsablaeufe', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-747', '', 't-vertrieb', 1, 't-vertriebsstruktur', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-748', '', 't-vertrieb', 1, 't-vertriebsqualitaet', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-749', '', 't-vertrieb', 1, 't-vertriebsablaeufe', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-750', '', 't-ertraege', 1, 't-wirtschaftlichkeit', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-751', '', 't-ertraege', 1, 't-ertragslage', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-752', '', 't-ertraege', 1, 't-zielabweichungen', 1);

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-753', '', 't-produkte', 1, 't-produktleistungen', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-754', '', 't-produkte', 1, 't-kundennutzen', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-755', '', 't-produkte', 1, 't-marktabweichungen', 1);

-- ===========================================================================



------------
--- View ---
------------



--- template
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-businesscheck', 1, 330, 260);

--- assessment layers
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-chancen', 1, 350, 110);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-plaene', 1, 350, 210);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-ablaeufe', 1, 350, 310);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-ergebnisse', 1, 350, 410);

--- top-level criteria
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-ziele', 1, 200, 110);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-management', 1, 200, 210);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-betrieb', 1, 200, 310);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-ertraege', 1, 200, 410);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-produkte', 1, 475, 410);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-vertrieb', 1, 475, 310);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-marketing', 1, 475, 210);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-markt', 1, 475, 110);

--- second-level criteria
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-innovationspotenziale', 1, 93, 78);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-unternehmenszukunft', 1, 63, 98);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-unternehmenspartner', 1, 31, 110);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-wettbewerb-zugang', 1, 588, 98);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-marktpartner', 1, 560, 117);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-markterwartungen', 1, 532, 133);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-produktplanung', 1, 81, 176);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-geschaeftsleitung', 1, 51, 187);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-techn-loesungen', 1, 23, 199);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-kommunikation', 1, 555, 215);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-produktmarketing', 1, 582, 197);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-preisattraktivitaet', 1, 528, 228);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-betriebsstruktur', 1, 18, 355);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-betriebsqualitaet', 1, 79, 316);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-betriebsablaeufe', 1, 48, 335);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-vertriebsstruktur', 1, 591, 288);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-vertriebsqualitaet', 1, 565, 306);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-vertriebsablaeufe', 1, 537, 323);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-wirtschaftlichkeit', 1, 85, 419);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-ertragslage', 1, 54, 434);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-zielabweichungen', 1, 22, 449);

INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-produktleistungen', 1, 567, 409);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-kundennutzen', 1, 540, 427);
INSERT INTO ViewTopic VALUES ('t-businesscheck', 1, 't-marktabweichungen', 1, 595, 390);

--- associations

--- from template to assessment layers
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-650', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-651', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-652', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-653', 1);

--- from template to top-level criteria
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-642', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-643', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-644', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-645', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-646', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-647', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-648', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-649', 1);

--- from top-level criteria to second-level criteria
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-732', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-733', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-734', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-735', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-736', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-737', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-738', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-739', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-740', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-741', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-742', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-743', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-744', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-745', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-746', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-747', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-748', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-749', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-750', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-751', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-752', 1);

INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-753', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-754', 1);
INSERT INTO ViewAssociation VALUES ('t-businesscheck', 1, 'a-755', 1);

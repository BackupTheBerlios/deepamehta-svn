------------------------
--- Whois topic type ---
------------------------



--- "Whois" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-whoistopic','Whois');
-- set properties
INSERT INTO TopicProp VALUES ('tt-whoistopic', 1, 'Name', 'Whois');
INSERT INTO TopicProp VALUES ('tt-whoistopic', 1, 'Icon', 'whois.gif');
INSERT INTO TopicProp VALUES ('tt-whoistopic', 1, 'Unique Topic Names', 'on');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-143', '', 'tt-whoistopic', 1, 'pp-wit_server', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-144', '', 'tt-whoistopic', 1, 'pp-wit_domains', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-whoistopiccontainer','Whois Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-whoistopiccontainer', 1, 'Name', 'Whois Search');
INSERT INTO TopicProp VALUES ('tt-whoistopiccontainer', 1, 'Icon', 'whoiscontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-128', '', 'tt-topiccontainer', 1, 'tt-whoistopiccontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-129', '', 'tt-whoistopiccontainer', 1, 'tt-whoistopic', 1);



--- whois servers ---

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois1','Whois Server 1');
INSERT INTO TopicProp VALUES ('tt-whois1', 1, 'Server', 'whois.crsnic.net');
INSERT INTO TopicProp VALUES ('tt-whois1', 1, 'Domains', 'com,net,org,edu');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois2','Whois Server 2');
INSERT INTO TopicProp VALUES ('tt-whois2', 1, 'Server', 'whois.ripe.net');
INSERT INTO TopicProp VALUES
 ('tt-whois2', 1, 'Domains', 'al,dz,ad,am,at,az,by,be,ba,bg,hr,cy,cz,eg,ee,fo,fi,ga,gm,ge,gr,gl,va,hu,il,jo,lv,lt,lu,mk,mt,md,mc,ma,pl,pt,ro,sm,sk,si,es,tn,tr,ua,gb,yu');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois3','Whois Server 3');
INSERT INTO TopicProp VALUES ('tt-whois3', 1, 'Server', 'whois.nic.mil');
INSERT INTO TopicProp VALUES ('tt-whois3', 1, 'Domains', 'mil');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois4','Whois Server 4');
INSERT INTO TopicProp VALUES ('tt-whois4', 1, 'Server', 'whois.nic.gov');
INSERT INTO TopicProp VALUES ('tt-whois4', 1, 'Domains', 'gov');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois5','Whois Server 5');
INSERT INTO TopicProp VALUES ('tt-whois5', 1, 'Server', 'whois.nic.uk');
INSERT INTO TopicProp VALUES ('tt-whois5', 1, 'Domains', 'uk');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois6','Whois Server 6');
INSERT INTO TopicProp VALUES ('tt-whois6', 1, 'Server', 'whois.nic.as');
INSERT INTO TopicProp VALUES ('tt-whois6', 1, 'Domains', 'as');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois7','Whois Server 7');
INSERT INTO TopicProp VALUES ('tt-whois7', 1, 'Server', 'whois.nic.ac');
INSERT INTO TopicProp VALUES ('tt-whois7', 1, 'Domains', 'ac');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois8','Whois Server 8');
INSERT INTO TopicProp VALUES ('tt-whois8', 1, 'Server', 'whois.aunic.net');
INSERT INTO TopicProp VALUES ('tt-whois8', 1, 'Domains', 'au');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois9','Whois Server 9');
INSERT INTO TopicProp VALUES ('tt-whois9', 1, 'Server', 'whois.nic.br');
INSERT INTO TopicProp VALUES ('tt-whois9', 1, 'Domains', 'br');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois10','Whois Server 10');
INSERT INTO TopicProp VALUES ('tt-whois10', 1, 'Server', 'whois.cira.net');
INSERT INTO TopicProp VALUES ('tt-whois10', 1, 'Domains', 'ca');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois11','Whois Server 11');
INSERT INTO TopicProp VALUES ('tt-whois11', 1, 'Server', 'whois.cnnic.net.cn');
INSERT INTO TopicProp VALUES ('tt-whois11', 1, 'Domains', 'cn');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois12','Whois Server 12');
INSERT INTO TopicProp VALUES ('tt-whois12', 1, 'Server', 'whois.niccx.com');
INSERT INTO TopicProp VALUES ('tt-whois12', 1, 'Domains', 'cx');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois13','Whois Server 13');
INSERT INTO TopicProp VALUES ('tt-whois13', 1, 'Server', 'whois.nic.cc');
INSERT INTO TopicProp VALUES ('tt-whois13', 1, 'Domains', 'cc');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois14','Whois Server 14');
INSERT INTO TopicProp VALUES ('tt-whois14', 1, 'Server', 'whois.ck-nic.org.ck');
INSERT INTO TopicProp VALUES ('tt-whois14', 1, 'Domains', 'ck');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois15','Whois Server 15');
INSERT INTO TopicProp VALUES ('tt-whois15', 1, 'Server', 'whois.dk-hostmaster.dk');
INSERT INTO TopicProp VALUES ('tt-whois15', 1, 'Domains', 'dk');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois16','Whois Server 16');
INSERT INTO TopicProp VALUES ('tt-whois16', 1, 'Server', 'ns.nic.do');
INSERT INTO TopicProp VALUES ('tt-whois16', 1, 'Domains', 'do');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois17','Whois Server 17');
INSERT INTO TopicProp VALUES ('tt-whois17', 1, 'Server', 'whois.usp.ac.fj');
INSERT INTO TopicProp VALUES ('tt-whois17', 1, 'Domains', 'fj');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois18','Whois Server 18');
INSERT INTO TopicProp VALUES ('tt-whois18', 1, 'Server', 'whois.nic.fr');
INSERT INTO TopicProp VALUES ('tt-whois18', 1, 'Domains', 'fr');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois19','Whois Server 19');
INSERT INTO TopicProp VALUES ('tt-whois19', 1, 'Server', 'whois.adamsnames.tc');
INSERT INTO TopicProp VALUES ('tt-whois19', 1, 'Domains', 'tf');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois20','Whois Server 20');
INSERT INTO TopicProp VALUES ('tt-whois20', 1, 'Server', 'whois.denic.de');
INSERT INTO TopicProp VALUES ('tt-whois20', 1, 'Domains', 'de');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois21','Whois Server 21');
INSERT INTO TopicProp VALUES ('tt-whois21', 1, 'Server', 'whois.nic.hm');
INSERT INTO TopicProp VALUES ('tt-whois21', 1, 'Domains', 'hm');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois22','Whois Server 22');
INSERT INTO TopicProp VALUES ('tt-whois22', 1, 'Server', 'whois.hknic.net.hk');
INSERT INTO TopicProp VALUES ('tt-whois22', 1, 'Domains', 'hk');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois23','Whois Server 23');
INSERT INTO TopicProp VALUES ('tt-whois23', 1, 'Server', 'whois.isnet.is');
INSERT INTO TopicProp VALUES ('tt-whois23', 1, 'Domains', 'is');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois24','Whois Server 24');
INSERT INTO TopicProp VALUES ('tt-whois24', 1, 'Server', 'whois.idnic.net.id');
INSERT INTO TopicProp VALUES ('tt-whois24', 1, 'Domains', 'id');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois25','Whois Server 25');
INSERT INTO TopicProp VALUES ('tt-whois25', 1, 'Server', 'whois.domainregistry.ie');
INSERT INTO TopicProp VALUES ('tt-whois25', 1, 'Domains', 'ie');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois26','Whois Server 26');
INSERT INTO TopicProp VALUES ('tt-whois26', 1, 'Server', 'whois.nic.it');
INSERT INTO TopicProp VALUES ('tt-whois26', 1, 'Domains', 'it');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois27','Whois Server 27');
INSERT INTO TopicProp VALUES ('tt-whois27', 1, 'Server', 'whois.nic.ad.jp');
INSERT INTO TopicProp VALUES ('tt-whois27', 1, 'Domains', 'jp');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois28','Whois Server 28');
INSERT INTO TopicProp VALUES ('tt-whois28', 1, 'Server', 'whois.domain.kz');
INSERT INTO TopicProp VALUES ('tt-whois28', 1, 'Domains', 'kz');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois29','Whois Server 29');
INSERT INTO TopicProp VALUES ('tt-whois29', 1, 'Server', 'whois.krnic.net');
INSERT INTO TopicProp VALUES ('tt-whois29', 1, 'Domains', 'kr');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois30','Whois Server 30');
INSERT INTO TopicProp VALUES ('tt-whois30', 1, 'Server', 'whois.domain.kg');
INSERT INTO TopicProp VALUES ('tt-whois30', 1, 'Domains', 'kg');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois31','Whois Server 31');
INSERT INTO TopicProp VALUES ('tt-whois31', 1, 'Server', 'whois.nic.li');
INSERT INTO TopicProp VALUES ('tt-whois31', 1, 'Domains', 'li');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois32','Whois Server 32');
INSERT INTO TopicProp VALUES ('tt-whois32', 1, 'Server', 'whois.nic.mx');
INSERT INTO TopicProp VALUES ('tt-whois32', 1, 'Domains', 'mx');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois33','Whois Server 33');
INSERT INTO TopicProp VALUES ('tt-whois33', 1, 'Server', 'whois.adamsnames.tc');
INSERT INTO TopicProp VALUES ('tt-whois33', 1, 'Domains', 'ms');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois34','Whois Server 34');
INSERT INTO TopicProp VALUES ('tt-whois34', 1, 'Server', 'whois.nic.mm');
INSERT INTO TopicProp VALUES ('tt-whois34', 1, 'Domains', 'mm');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois35','Whois Server 35');
INSERT INTO TopicProp VALUES ('tt-whois35', 1, 'Server', 'whois.domain-registry.nl');
INSERT INTO TopicProp VALUES ('tt-whois35', 1, 'Domains', 'nl');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois36','Whois Server 36');
INSERT INTO TopicProp VALUES ('tt-whois36', 1, 'Server', 'whois.domainz.net.nz');
INSERT INTO TopicProp VALUES ('tt-whois36', 1, 'Domains', 'nz');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois37','Whois Server 37');
INSERT INTO TopicProp VALUES ('tt-whois37', 1, 'Server', 'pgebrehiwot.iat.cnr.it');
INSERT INTO TopicProp VALUES ('tt-whois37', 1, 'Domains', 'ng');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois38','Whois Server 38');
INSERT INTO TopicProp VALUES ('tt-whois38', 1, 'Server', 'whois.nic.nu');
INSERT INTO TopicProp VALUES ('tt-whois38', 1, 'Domains', 'nu');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois39','Whois Server 39');
INSERT INTO TopicProp VALUES ('tt-whois39', 1, 'Server', 'whois.norid.no');
INSERT INTO TopicProp VALUES ('tt-whois39', 1, 'Domains', 'no');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois40','Whois Server 40');
INSERT INTO TopicProp VALUES ('tt-whois40', 1, 'Server', 'whois.pknic.nrt.pk');
INSERT INTO TopicProp VALUES ('tt-whois40', 1, 'Domains', 'pk');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois41','Whois Server 41');
INSERT INTO TopicProp VALUES ('tt-whois41', 1, 'Server', 'whois.worldsite.ws');
INSERT INTO TopicProp VALUES ('tt-whois41', 1, 'Domains', 'ws');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois42','Whois Server 42');
INSERT INTO TopicProp VALUES ('tt-whois42', 1, 'Server', 'whois.nic.st');
INSERT INTO TopicProp VALUES ('tt-whois42', 1, 'Domains', 'st');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois43','Whois Server 43');
INSERT INTO TopicProp VALUES ('tt-whois43', 1, 'Server', 'whois.nic.net.sg');
INSERT INTO TopicProp VALUES ('tt-whois43', 1, 'Domains', 'sg');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois44','Whois Server 44');
INSERT INTO TopicProp VALUES ('tt-whois44', 1, 'Server', 'whois.frd.ac.za');
INSERT INTO TopicProp VALUES ('tt-whois44', 1, 'Domains', 'za');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois45','Whois Server 45');
INSERT INTO TopicProp VALUES ('tt-whois45', 1, 'Server', 'whois.nic.lk');
INSERT INTO TopicProp VALUES ('tt-whois45', 1, 'Domains', 'lk');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois46','Whois Server 46');
INSERT INTO TopicProp VALUES ('tt-whois46', 1, 'Server', 'whois.nic.sh');
INSERT INTO TopicProp VALUES ('tt-whois46', 1, 'Domains', 'sh');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois47','Whois Server 47');
INSERT INTO TopicProp VALUES ('tt-whois47', 1, 'Server', 'whois.nic-se.se');
INSERT INTO TopicProp VALUES ('tt-whois47', 1, 'Domains', 'se');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois48','Whois Server 48');
INSERT INTO TopicProp VALUES ('tt-whois48', 1, 'Server', 'whois.nic.ch');
INSERT INTO TopicProp VALUES ('tt-whois48', 1, 'Domains', 'ch');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois49','Whois Server 49');
INSERT INTO TopicProp VALUES ('tt-whois49', 1, 'Server', 'whois.twnic.net.tw');
INSERT INTO TopicProp VALUES ('tt-whois49', 1, 'Domains', 'tw');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois50','Whois Server 50');
INSERT INTO TopicProp VALUES ('tt-whois50', 1, 'Server', 'whois.thnic.net');
INSERT INTO TopicProp VALUES ('tt-whois50', 1, 'Domains', 'th');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois51','Whois Server 51');
INSERT INTO TopicProp VALUES ('tt-whois51', 1, 'Server', 'whois.tonic.to');
INSERT INTO TopicProp VALUES ('tt-whois51', 1, 'Domains', 'to');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois52','Whois Server 52');
INSERT INTO TopicProp VALUES ('tt-whois52', 1, 'Server', 'whois.nic.tm');
INSERT INTO TopicProp VALUES ('tt-whois52', 1, 'Domains', 'tm');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois53','Whois Server 53');
INSERT INTO TopicProp VALUES ('tt-whois53', 1, 'Server', 'whois.adamsnames.tc');
INSERT INTO TopicProp VALUES ('tt-whois53', 1, 'Domains', 'tc');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois54','Whois Server 54');
INSERT INTO TopicProp VALUES ('tt-whois54', 1, 'Server', 'whois.isi.edu');
INSERT INTO TopicProp VALUES ('tt-whois54', 1, 'Domains', 'us');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois55','Whois Server 55');
INSERT INTO TopicProp VALUES ('tt-whois55', 1, 'Server', 'whois.adamsnames.tc');
INSERT INTO TopicProp VALUES ('tt-whois55', 1, 'Domains', 'vg');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois56','Whois Server 56');
INSERT INTO TopicProp VALUES ('tt-whois56', 1, 'Server', 'whois.isi.edu');
INSERT INTO TopicProp VALUES ('tt-whois56', 1, 'Domains', 'int');

INSERT INTO Topic VALUES
 ('tt-whoistopic', 1, 1, 'tt-whois57','Whois Server 57');
INSERT INTO TopicProp VALUES ('tt-whois57', 1, 'Server', 'whois.adamsnames.tc');
INSERT INTO TopicProp VALUES ('tt-whois57', 1, 'Domains', 'gs');



INSERT INTO TopicProp VALUES ('tt-whois1', 1, 'Name', 'Whois Server 1');
INSERT INTO TopicProp VALUES ('tt-whois10', 1, 'Name', 'Whois Server 10');
INSERT INTO TopicProp VALUES ('tt-whois11', 1, 'Name', 'Whois Server 11');
INSERT INTO TopicProp VALUES ('tt-whois12', 1, 'Name', 'Whois Server 12');
INSERT INTO TopicProp VALUES ('tt-whois13', 1, 'Name', 'Whois Server 13');
INSERT INTO TopicProp VALUES ('tt-whois14', 1, 'Name', 'Whois Server 14');
INSERT INTO TopicProp VALUES ('tt-whois15', 1, 'Name', 'Whois Server 15');
INSERT INTO TopicProp VALUES ('tt-whois16', 1, 'Name', 'Whois Server 16');
INSERT INTO TopicProp VALUES ('tt-whois17', 1, 'Name', 'Whois Server 17');
INSERT INTO TopicProp VALUES ('tt-whois18', 1, 'Name', 'Whois Server 18');
INSERT INTO TopicProp VALUES ('tt-whois19', 1, 'Name', 'Whois Server 19');
INSERT INTO TopicProp VALUES ('tt-whois2', 1, 'Name', 'Whois Server 2');
INSERT INTO TopicProp VALUES ('tt-whois20', 1, 'Name', 'Whois Server 20');
INSERT INTO TopicProp VALUES ('tt-whois21', 1, 'Name', 'Whois Server 21');
INSERT INTO TopicProp VALUES ('tt-whois22', 1, 'Name', 'Whois Server 22');
INSERT INTO TopicProp VALUES ('tt-whois23', 1, 'Name', 'Whois Server 23');
INSERT INTO TopicProp VALUES ('tt-whois24', 1, 'Name', 'Whois Server 24');
INSERT INTO TopicProp VALUES ('tt-whois25', 1, 'Name', 'Whois Server 25');
INSERT INTO TopicProp VALUES ('tt-whois26', 1, 'Name', 'Whois Server 26');
INSERT INTO TopicProp VALUES ('tt-whois27', 1, 'Name', 'Whois Server 27');
INSERT INTO TopicProp VALUES ('tt-whois28', 1, 'Name', 'Whois Server 28');
INSERT INTO TopicProp VALUES ('tt-whois29', 1, 'Name', 'Whois Server 29');
INSERT INTO TopicProp VALUES ('tt-whois3', 1, 'Name', 'Whois Server 3');
INSERT INTO TopicProp VALUES ('tt-whois30', 1, 'Name', 'Whois Server 30');
INSERT INTO TopicProp VALUES ('tt-whois31', 1, 'Name', 'Whois Server 31');
INSERT INTO TopicProp VALUES ('tt-whois32', 1, 'Name', 'Whois Server 32');
INSERT INTO TopicProp VALUES ('tt-whois33', 1, 'Name', 'Whois Server 33');
INSERT INTO TopicProp VALUES ('tt-whois34', 1, 'Name', 'Whois Server 34');
INSERT INTO TopicProp VALUES ('tt-whois35', 1, 'Name', 'Whois Server 35');
INSERT INTO TopicProp VALUES ('tt-whois36', 1, 'Name', 'Whois Server 36');
INSERT INTO TopicProp VALUES ('tt-whois37', 1, 'Name', 'Whois Server 37');
INSERT INTO TopicProp VALUES ('tt-whois38', 1, 'Name', 'Whois Server 38');
INSERT INTO TopicProp VALUES ('tt-whois39', 1, 'Name', 'Whois Server 39');
INSERT INTO TopicProp VALUES ('tt-whois4', 1, 'Name', 'Whois Server 4');
INSERT INTO TopicProp VALUES ('tt-whois40', 1, 'Name', 'Whois Server 40');
INSERT INTO TopicProp VALUES ('tt-whois41', 1, 'Name', 'Whois Server 41');
INSERT INTO TopicProp VALUES ('tt-whois42', 1, 'Name', 'Whois Server 42');
INSERT INTO TopicProp VALUES ('tt-whois43', 1, 'Name', 'Whois Server 43');
INSERT INTO TopicProp VALUES ('tt-whois44', 1, 'Name', 'Whois Server 44');
INSERT INTO TopicProp VALUES ('tt-whois45', 1, 'Name', 'Whois Server 45');
INSERT INTO TopicProp VALUES ('tt-whois46', 1, 'Name', 'Whois Server 46');
INSERT INTO TopicProp VALUES ('tt-whois47', 1, 'Name', 'Whois Server 47');
INSERT INTO TopicProp VALUES ('tt-whois48', 1, 'Name', 'Whois Server 48');
INSERT INTO TopicProp VALUES ('tt-whois49', 1, 'Name', 'Whois Server 49');
INSERT INTO TopicProp VALUES ('tt-whois5', 1, 'Name', 'Whois Server 5');
INSERT INTO TopicProp VALUES ('tt-whois50', 1, 'Name', 'Whois Server 50');
INSERT INTO TopicProp VALUES ('tt-whois51', 1, 'Name', 'Whois Server 51');
INSERT INTO TopicProp VALUES ('tt-whois52', 1, 'Name', 'Whois Server 52');
INSERT INTO TopicProp VALUES ('tt-whois53', 1, 'Name', 'Whois Server 53');
INSERT INTO TopicProp VALUES ('tt-whois54', 1, 'Name', 'Whois Server 54');
INSERT INTO TopicProp VALUES ('tt-whois55', 1, 'Name', 'Whois Server 55');
INSERT INTO TopicProp VALUES ('tt-whois56', 1, 'Name', 'Whois Server 56');
INSERT INTO TopicProp VALUES ('tt-whois57', 1, 'Name', 'Whois Server 57');
INSERT INTO TopicProp VALUES ('tt-whois6', 1, 'Name', 'Whois Server 6');
INSERT INTO TopicProp VALUES ('tt-whois7', 1, 'Name', 'Whois Server 7');
INSERT INTO TopicProp VALUES ('tt-whois8', 1, 'Name', 'Whois Server 8');
INSERT INTO TopicProp VALUES ('tt-whois9', 1, 'Name', 'Whois Server 9');

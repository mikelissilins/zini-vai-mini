INSERT INTO games (id, title, description, locale, template, template_key, version, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000003',
    'Zini vai mini – lielais nometnes izaicinājums',
    '42 jautājumi ar vieglu sākumu, matemātiku, Bībeli un mazliet nometnes humora.',
    'lv', TRUE, 'camp-challenge', 0, NOW(), NOW()
);

WITH seed(name, color, position) AS (
    VALUES
        ('Bībele', '#2A9D8F', 0),
        ('Jēzus', '#F77F5B', 1),
        ('Matemātika', '#55B8CC', 2),
        ('Jūra', '#3F88C5', 3),
        ('Nometne', '#5CA67A', 4),
        ('Jautrās mīklas', '#F2A65A', 5)
)
INSERT INTO categories (id, game_id, name, color, position)
SELECT gen_random_uuid(), '00000000-0000-0000-0000-000000000003', name, color, position
FROM seed;

WITH seed(category_name, points, question_type, prompt, answer, explanation) AS (
    VALUES
        ('Bībele', 10, 'MULTIPLE_CHOICE', 'Kurš uzbūvēja lielu šķirstu, lai izglābtu ģimeni un dzīvniekus?', 'Noa', 'Pavediens: viņam laikam bija pasaulē garākais dzīvnieku saraksts.'),
        ('Bībele', 20, 'FREE_TEXT', 'Kā sauca milzi, kuru ar lingu uzvarēja Dāvids?', 'Goliāts', 'Pavediens: viņš nebija priecīgs, redzot piecus gludus akmentiņus.'),
        ('Bībele', 30, 'FREE_TEXT', 'Cik dienas Jona pavadīja lielās zivs vēderā?', 'Trīs dienas', 'Pavediens: tā noteikti nebija viņa sapņu kruīza kajīte.'),
        ('Bībele', 40, 'FREE_TEXT', 'Kura drosmīga sieviete kļuva par ķēniņieni un glāba savu tautu?', 'Estere', 'Pavediens: viņas radinieks Mordohajs viņu iedrošināja.'),
        ('Bībele', 50, 'MULTIPLE_CHOICE', 'Kura Bībeles grāmata sākas ar pasaules radīšanu?', '1. Mozus grāmata', 'Pavediens: pašā sākumā ir gaisma, nevis nometnes brokastis.'),
        ('Bībele', 60, 'FREE_TEXT', 'Kā sauc pravieti, kurš redzēja vīziju par atdzīvojušiem sausiem kauliem?', 'Ecēhiēls', 'Pavediens: meklē Vecās Derības praviešu grāmatās.'),
        ('Bībele', 70, 'FREE_TEXT', 'Kā sauca ķēniņu, kurš redzēja uz pils sienas noslēpumainu rakstu, ko iztulkoja Daniēls?', 'Belsacars', 'Pavediens: atbilde nav Nebukadnēcars, lai gan viņš ir no tā paša stāstu plaukta.'),

        ('Jēzus', 10, 'MULTIPLE_CHOICE', 'Kur piedzima Jēzus?', 'Betlēmē', 'Pavediens: gani Viņu atrada silītē.'),
        ('Jēzus', 20, 'FREE_TEXT', 'Kā sauca Jēzus māti?', 'Marija', 'Pavediens: eņģelis Gabriēls viņai nesa priecīgu ziņu.'),
        ('Jēzus', 30, 'FREE_TEXT', 'Ko Jēzus pārvērta vīnā kāzās Kānā?', 'Ūdeni', 'Pavediens: kalpi piepildīja lielus traukus.'),
        ('Jēzus', 40, 'FREE_TEXT', 'Cik apustuļus Jēzus īpaši izraudzījās?', '12', 'Pavediens: tikpat, cik Israēla ciltis.'),
        ('Jēzus', 50, 'MULTIPLE_CHOICE', 'Kurš māceklis gribēja redzēt Jēzus rētas, pirms noticēja augšāmcelšanai?', 'Toms', 'Pavediens: viņš nav tas pats, kas Toms no nometnes virtuves.'),
        ('Jēzus', 60, 'FREE_TEXT', 'Kā sauca vīru, kuru Jēzus uzcēla no kapa pēc četrām dienām?', 'Lācars', 'Pavediens: viņa māsu sauca Marta un Marija.'),
        ('Jēzus', 70, 'FREE_TEXT', 'Kā sauca muitnieku, kurš uzkāpa kokā, lai redzētu Jēzu?', 'Caķejs', 'Pavediens: viņš bija neliela auguma, bet ļoti apņēmīgs.'),

        ('Matemātika', 10, 'MULTIPLE_CHOICE', 'Cik ir 2 + 2?', '4', 'Pavediens: pat nometnes pavārs šo sarēķina pirms kafijas.'),
        ('Matemātika', 20, 'FREE_TEXT', 'Cik ir 6 × 7?', '42', 'Pavediens: šis skaitlis ir arī atbilde uz dzīvi, Visumu un visu pārējo kādā slavenā grāmatā.'),
        ('Matemātika', 30, 'FREE_TEXT', 'Cik ir 100 − 37?', '63', 'Pavediens: no simta aiziet trīs desmiti un vēl septiņi.'),
        ('Matemātika', 40, 'FREE_TEXT', 'Cik malu ir trijstūrim?', '3', 'Pavediens: ja ir četras, tas vairs ļoti cenšas būt kvadrāts.'),
        ('Matemātika', 50, 'MULTIPLE_CHOICE', 'Cik ir 15% no 200?', '30', 'Pavediens: 10% ir 20, bet 5% ir 10.'),
        ('Matemātika', 60, 'FREE_TEXT', 'Kāds ir nākamais pirmskaitlis pēc 97?', '101', 'Pavediens: 98, 99 un 100 nav īstie varoņi.'),
        ('Matemātika', 70, 'FREE_TEXT', 'Kāds ir skaitļa 7^2026 pēdējais cipars?', '9', 'Pavediens: pēdējie cipari atkārtojas ciklā 7, 9, 3, 1.'),

        ('Jūra', 10, 'MULTIPLE_CHOICE', 'Kāda ir Baltijas jūras ūdens garša?', 'Sāļa', 'Pavediens: lūdzu, nepārbaudi to ar pilnu muti.'),
        ('Jūra', 20, 'FREE_TEXT', 'Kā sauc cilvēku, kurš vada kuģi?', 'Kapteinis', 'Pavediens: viņš parasti nekliedz “ātrāk!” uz ūdensslēpēm.'),
        ('Jūra', 30, 'FREE_TEXT', 'Kā sauc jūru pie Latvijas krastiem?', 'Baltijas jūra', 'Pavediens: tā nav Vidusjūra, lai cik silta būtu jūlijā.'),
        ('Jūra', 40, 'FREE_TEXT', 'Ko mēra kuģa ātruma vienība “mezgls”?', 'Jūras jūdzes stundā', 'Pavediens: tas nav tas pats mezgls, kas kurpju auklās.'),
        ('Jūra', 50, 'MULTIPLE_CHOICE', 'Kuru jūru Dievs pašķīra, lai Israēla tauta varētu pāriet sausām kājām?', 'Sarkano jūru', 'Pavediens: Mozus izstiepa roku, bet viņam nebija vajadzīgs tunelis.'),
        ('Jūra', 60, 'FREE_TEXT', 'Kā sauc lielāko okeānu uz Zemes?', 'Klusais okeāns', 'Pavediens: nosaukums ir mierīgs, bet okeāns var būt ļoti nopietns.'),
        ('Jūra', 70, 'FREE_TEXT', 'Kuģis brauc ar 3 mezgliem 2 stundas. Cik jūras jūdzes tas nobrauc?', '6 jūras jūdzes', 'Pavediens: ātrums reiz laiks. Kapteinis jau gaida atbildi.'),

        ('Nometne', 10, 'MULTIPLE_CHOICE', 'Ko dara laba komanda, ja pārgājienā kāds atpaliek?', 'Apstājas un paliek kopā', 'Pavediens: nevienu neatstāj aiz muguras, pat ne lēnāko čipsu ēdāju.'),
        ('Nometne', 20, 'FREE_TEXT', 'Nosauc divas lietas, kas noder lietainā nometnes dienā.', 'Lietus jaka un ūdensizturīgi apavi', 'Pavediens: saulesbrilles vien šoreiz neizglābs.'),
        ('Nometne', 30, 'FREE_TEXT', 'Kā skan zelta likums par izturēšanos pret citiem?', 'Dari citiem to, ko vēlies, lai viņi dara tev', 'Pavediens: tas darbojas arī rindā pēc pankūkām.'),
        ('Nometne', 40, 'FREE_TEXT', 'Ko darīt, ja pie ugunskura pamani, ka uguns kļūst pārāk liela?', 'Nekavējoties pasaukt vadītāju un turēt ūdeni tuvumā', 'Pavediens: nemēģini kļūt par ugunsdzēsēju ar zeķi.'),
        ('Nometne', 50, 'MULTIPLE_CHOICE', 'Kura ir labākā komandas uzkoda karstā dienā?', 'Ūdens un augļi', 'Pavediens: trīs enerģijas dzērieni un konfekšu kalns nav slepenais sporta režīms.'),
        ('Nometne', 60, 'FREE_TEXT', 'Ko dara, ja vadītājs saka “pēc piecām minūtēm ejam”, bet komandas biedrs vēl meklē vienu zeķi?', 'Palīdz atrast zeķi un sagatavojas laicīgi', 'Pavediens: laba komanda nepadara zeķi par stundu garu piedzīvojumu.'),
        ('Nometne', 70, 'FREE_TEXT', 'Trīs teltīm vajag pa divām lukturītēm. Viena lukturīte salūzt. Cik strādājošu lukturīšu paliek?', '5', 'Pavediens: sākumā ir sešas. Viena aiziet pelnītā atpūtā.'),

        ('Jautrās mīklas', 10, 'MULTIPLE_CHOICE', 'Kas kļūst slapjāks, jo vairāk tas žāvē?', 'Dvielis', 'Pavediens: tas noteikti nav lietussargs ar sliktiem nodomiem.'),
        ('Jautrās mīklas', 20, 'FREE_TEXT', 'Kas var piepildīt visu istabu, bet neaizņem vietu?', 'Gaisma', 'Pavediens: ieslēdz lampu un skaties.'),
        ('Jautrās mīklas', 30, 'FREE_TEXT', 'Kam ir taustiņi, bet tas nevar atslēgt nevienas durvis?', 'Klavierēm', 'Pavediens: tās var spēlēt dziesmas, nevis atslēgt virtuvi.'),
        ('Jautrās mīklas', 40, 'FREE_TEXT', 'Kam ir daudz zobu, bet tas nevar iekost?', 'Ķemmei', 'Pavediens: tā labāk jūtas matos nekā pie pusdienu galda.'),
        ('Jautrās mīklas', 50, 'MULTIPLE_CHOICE', 'Kas skrien, bet tam nav kāju?', 'Ūdens', 'Pavediens: tas skrien pa upi, nevis nometnes stafeti.'),
        ('Jautrās mīklas', 60, 'FREE_TEXT', 'Kas ir vienu reizi vārdā “minūtē”, divas reizes vārdā “momentā”, bet nevienu reizi vārdos “tūkstoš gados”?', 'Burts M', 'Pavediens: šis ir burtu, nevis kalendāra jautājums.'),
        ('Jautrās mīklas', 70, 'FREE_TEXT', 'Es runāju bez mutes un dzirdu bez ausīm. Man nav ķermeņa, bet es atdzīvojos vējā. Kas es esmu?', 'Atbalss', 'Pavediens: pamēģini kliegt pie klints, bet ne naktsmiera laikā.')
)
INSERT INTO questions (id, category_id, points, question_type, prompt, answer, explanation)
SELECT gen_random_uuid(), c.id, seed.points, seed.question_type, seed.prompt, seed.answer, seed.explanation
FROM seed
JOIN categories c ON c.name = seed.category_name
WHERE c.game_id = '00000000-0000-0000-0000-000000000003';

WITH option_seed(category_name, points, position, text, correct) AS (
    VALUES
        ('Bībele', 10, 0, 'Noa', TRUE), ('Bībele', 10, 1, 'Dāvids', FALSE), ('Bībele', 10, 2, 'Nometnes pavārs', FALSE), ('Bībele', 10, 3, 'Jona ar snorkeli', FALSE),
        ('Bībele', 50, 0, 'Psalmi', FALSE), ('Bībele', 50, 1, '1. Mozus grāmata', TRUE), ('Bībele', 50, 2, 'Atklāsmes grāmata', FALSE), ('Bībele', 50, 3, 'Nometnes noteikumu burtnīca', FALSE),
        ('Jēzus', 10, 0, 'Nācaretē', FALSE), ('Jēzus', 10, 1, 'Betlēmē', TRUE), ('Jēzus', 10, 2, 'Rīgā', FALSE), ('Jēzus', 10, 3, 'Telts numur 7', FALSE),
        ('Jēzus', 50, 0, 'Pēteris', FALSE), ('Jēzus', 50, 1, 'Toms', TRUE), ('Jēzus', 50, 2, 'Jūda', FALSE), ('Jēzus', 50, 3, 'Toms no virtuves', FALSE),
        ('Matemātika', 10, 0, '3', FALSE), ('Matemātika', 10, 1, '4', TRUE), ('Matemātika', 10, 2, '22', FALSE), ('Matemātika', 10, 3, 'Zivs', FALSE),
        ('Matemātika', 50, 0, '15', FALSE), ('Matemātika', 50, 1, '20', FALSE), ('Matemātika', 50, 2, '30', TRUE), ('Matemātika', 50, 3, '200, jo procenti ir noslēpums', FALSE),
        ('Jūra', 10, 0, 'Sāļa', TRUE), ('Jūra', 10, 1, 'Zemeņu', FALSE), ('Jūra', 10, 2, 'Pankūku', FALSE), ('Jūra', 10, 3, 'Pilnīgi bezgaršīga', FALSE),
        ('Jūra', 50, 0, 'Baltijas jūru', FALSE), ('Jūra', 50, 1, 'Sarkano jūru', TRUE), ('Jūra', 50, 2, 'Nometnes dīķi', FALSE), ('Jūra', 50, 3, 'Peldbaseinu', FALSE),
        ('Nometne', 10, 0, 'Skrien tālāk, lai uzvarētu', FALSE), ('Nometne', 10, 1, 'Apstājas un paliek kopā', TRUE), ('Nometne', 10, 2, 'Sūta balodi ar ziņu', FALSE), ('Nometne', 10, 3, 'Slēpj pārgājiena karti', FALSE),
        ('Nometne', 50, 0, 'Tikai konfektes', FALSE), ('Nometne', 50, 1, 'Ūdens un augļi', TRUE), ('Nometne', 50, 2, 'Trīs enerģijas dzērieni', FALSE), ('Nometne', 50, 3, 'Smilšu sauja', FALSE),
        ('Jautrās mīklas', 10, 0, 'Dvielis', TRUE), ('Jautrās mīklas', 10, 1, 'Zivs', FALSE), ('Jautrās mīklas', 10, 2, 'Nometnes suns', FALSE), ('Jautrās mīklas', 10, 3, 'Ugunsdzēsēja šļūtene', FALSE),
        ('Jautrās mīklas', 50, 0, 'Vējš', FALSE), ('Jautrās mīklas', 50, 1, 'Ūdens', TRUE), ('Jautrās mīklas', 50, 2, 'Brokastu putra', FALSE), ('Jautrās mīklas', 50, 3, 'Slinkais pingvīns', FALSE)
)
INSERT INTO question_options (id, question_id, text, position, correct)
SELECT gen_random_uuid(), q.id, option_seed.text, option_seed.position, option_seed.correct
FROM option_seed
JOIN categories c ON c.name = option_seed.category_name
JOIN questions q ON q.category_id = c.id AND q.points = option_seed.points
WHERE c.game_id = '00000000-0000-0000-0000-000000000003';

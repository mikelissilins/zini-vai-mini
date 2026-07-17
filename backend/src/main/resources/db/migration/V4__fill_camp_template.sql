CREATE TEMP TABLE camp_template_targets ON COMMIT DROP AS
SELECT g.id AS game_id
FROM games g
JOIN categories c ON c.game_id = g.id
JOIN questions q ON q.category_id = c.id
GROUP BY g.id
HAVING COUNT(DISTINCT c.id) = 6
   AND COUNT(q.id) = 30
   AND COUNT(DISTINCT c.name) FILTER (
       WHERE c.name IN ('Bībele', 'Jēzus', 'Jūra', 'Nometne', 'Dziesmas', 'Mīklas')
   ) = 6
   AND BOOL_AND(BTRIM(q.prompt) = '')
   AND BOOL_AND(BTRIM(q.answer) = '');

WITH seed(category_name, points, question_type, prompt, answer, explanation) AS (
    VALUES
        ('Bībele', 10, 'MULTIPLE_CHOICE', 'Cik grāmatu ir Bībelē?', '66', 'Pavediens: Bībelei ir Vecā un Jaunā Derība. Saskaiti 39 un 27.'),
        ('Bībele', 20, 'FREE_TEXT', 'Kas pēc Dieva norādījuma uzbūvēja lielu šķirstu?', 'Noa', 'Pavediens: viņš šķirstā sapulcināja savu ģimeni un dzīvniekus pa pāriem.'),
        ('Bībele', 30, 'MULTIPLE_CHOICE', 'Kā sauc pirmo Bībeles grāmatu?', '1. Mozus grāmata', 'Pavediens: tajā lasāms par pasaules radīšanu, Ādamu un Ievu.'),
        ('Bībele', 40, 'FREE_TEXT', 'Kurš jaunais gans uzvarēja milzi Goliātu?', 'Dāvids', 'Pavediens: viņam bija linga, pieci akmentiņi un liela paļāvība uz Dievu.'),
        ('Bībele', 50, 'MULTIPLE_CHOICE', 'Kurā Bībeles grāmatā aprakstīti Dieva ieroči: ticības vairogs un pestīšanas bruņucepure?', 'Vēstulē efeziešiem', 'Pavediens: meklē Jaunās Derības vēstulē, 6. nodaļā.'),

        ('Jēzus', 10, 'MULTIPLE_CHOICE', 'Kurā pilsētā piedzima Jēzus?', 'Betlēmē', 'Pavediens: gani Viņu atrada silītē Dāvida pilsētā.'),
        ('Jēzus', 20, 'FREE_TEXT', 'Kā sauca Jēzus māti?', 'Marija', 'Pavediens: eņģelis Gabriēls viņai paziņoja priecīgo vēsti.'),
        ('Jēzus', 30, 'MULTIPLE_CHOICE', 'Cik apustuļus Jēzus īpaši izraudzījās?', '12', 'Pavediens: tikpat, cik Israēla ciltis.'),
        ('Jēzus', 40, 'FREE_TEXT', 'Kāds bija Jēzus pirmais brīnums kāzās Kānā?', 'Viņš pārvērta ūdeni vīnā', 'Pavediens: kalpi piepildīja lielus traukus ar ūdeni.'),
        ('Jēzus', 50, 'MULTIPLE_CHOICE', 'Apmēram cik cilvēku Jēzus pabaroja ar piecām maizēm un divām zivīm?', '5000', 'Pavediens: pēc maltītes vēl palika divpadsmit pilni grozi.'),

        ('Jūra', 10, 'FREE_TEXT', 'Ko Jēzus apsauca, kad mācekļu laivu apdraudēja lieli viļņi?', 'Vētru', 'Pavediens: pēc Jēzus vārdiem iestājās liels klusums.'),
        ('Jūra', 20, 'MULTIPLE_CHOICE', 'Kurš māceklis izkāpa no laivas un gāja pa ūdeni pretī Jēzum?', 'Pēteris', 'Pavediens: viņa vārds nozīmē klints.'),
        ('Jūra', 30, 'FREE_TEXT', 'Kuru jūru Dievs pašķīra, lai Israēla tauta varētu pāriet sausām kājām?', 'Sarkano jūru', 'Pavediens: Mozus izstiepa roku pār jūru.'),
        ('Jūra', 40, 'MULTIPLE_CHOICE', 'Kas norija pravieti Jonu pēc tam, kad viņu iemeta jūrā?', 'Liela zivs', 'Pavediens: Bībele nenosauc konkrētu zivs sugu.'),
        ('Jūra', 50, 'FREE_TEXT', 'Nosauc vismaz divus Jēzus mācekļus, kuri pirms tam bija zvejnieki.', 'Pēteris, Andrejs, Jēkabs vai Jānis', 'Pavediens: divi bija brāļi Sīmaņa Pētera ģimenē, divi - Cebedeja dēli.'),

        ('Nometne', 10, 'MULTIPLE_CHOICE', 'Ko kristīgā nometnē varam darīt pirms kopīgas maltītes?', 'Pateikties Dievam lūgšanā', 'Pavediens: arī Jēzus pateicās par maizi, pirms to dalīja.'),
        ('Nometne', 20, 'FREE_TEXT', 'Ko komandai darīt, ja pārgājienā kāds atpaliek?', 'Apstāties, palikt kopā un pateikt vadītājam', 'Pavediens: laba komanda neatstāj nevienu vienu pašu.'),
        ('Nometne', 30, 'MULTIPLE_CHOICE', 'Kura ir drošākā rīcība pie nometnes ugunskura?', 'Turēt ūdeni tuvumā un klausīt vadītāju', 'Pavediens: uguns ir draugs tikai tad, ja to pieskata.'),
        ('Nometne', 40, 'FREE_TEXT', 'Kā skan Jēzus zelta likums par izturēšanos pret citiem?', 'Dari citiem to, ko vēlies, lai viņi dara tev', 'Pavediens: padomā, kā tu pats gribētu, lai komandas biedri izturas pret tevi.'),
        ('Nometne', 50, 'MULTIPLE_CHOICE', 'Divi komandas biedri sastrīdas spēles laikā. Kāds ir labākais pirmais solis?', 'Mierīgi uzklausīt abus un meklēt izlīgumu', 'Pavediens: būt miera nesējam ir svarīgāk nekā pierādīt, kurš skaļāks.'),

        ('Dziesmas', 10, 'MULTIPLE_CHOICE', 'Kādu instrumentu spēlēja Dāvids?', 'Arfu', 'Pavediens: tās stīgas skanēja ķēniņa Saula namā.'),
        ('Dziesmas', 20, 'FREE_TEXT', 'Kuru Bībeles grāmatu veido daudzas dziesmas un lūgšanas?', 'Psalmi', 'Pavediens: tās nosaukums bieži redzams kopā ar kādu numuru.'),
        ('Dziesmas', 30, 'MULTIPLE_CHOICE', 'Ko 100. psalms aicina darīt Tam Kungam?', 'Gavilēt un kalpot ar prieku', 'Pavediens: šī nav klusa un bēdīga dziesma.'),
        ('Dziesmas', 40, 'FREE_TEXT', 'Kuri divi vīri cietumā pusnaktī lūdza Dievu un dziedāja slavas dziesmas?', 'Pāvils un Sīla', 'Pavediens: pēc tam notika zemestrīce un atvērās cietuma durvis.'),
        ('Dziesmas', 50, 'MULTIPLE_CHOICE', 'Kāpēc kristieši dzied slavas dziesmas?', 'Lai pateiktos Dievam un iedrošinātu citus', 'Pavediens: dziesma var būt gan lūgšana, gan kopīgs prieks.'),

        ('Mīklas', 10, 'FREE_TEXT', 'Es uzbūvēju milzīgu laivu, lai gan apkārt vēl nebija plūdu. Kas es esmu?', 'Noa', 'Pavediens: pēc plūdiem debesīs parādījās varavīksne.'),
        ('Mīklas', 20, 'MULTIPLE_CHOICE', 'Es biju milzis un baidīju veselu armiju, bet mani uzvarēja viens akmens. Kas es esmu?', 'Goliāts', 'Pavediens: pretinieks bija jauns gans.'),
        ('Mīklas', 30, 'FREE_TEXT', 'Trīs dienas pavadīju lielas zivs vēderā un pēc tam devos uz Ninivi. Kas es esmu?', 'Jona', 'Pavediens: sākumā es bēgu pretējā virzienā no Dieva dotā uzdevuma.'),
        ('Mīklas', 40, 'MULTIPLE_CHOICE', 'Es kļuvu par ķēniņieni un drosmīgi aizstāvēju savu tautu. Kas es esmu?', 'Estere', 'Pavediens: mans radinieks Mordohajs mani iedrošināja.'),
        ('Mīklas', 50, 'FREE_TEXT', 'Mans spēks bija saistīts ar matiem, un es cīnījos pret filistiešiem. Kas es esmu?', 'Simsons', 'Pavediens: Delīla centās atklāt mana spēka noslēpumu.')
)
UPDATE questions q
SET question_type = seed.question_type,
    prompt = seed.prompt,
    answer = seed.answer,
    explanation = seed.explanation
FROM categories c
JOIN camp_template_targets target ON target.game_id = c.game_id
JOIN seed ON seed.category_name = c.name
WHERE q.category_id = c.id
  AND q.points = seed.points;

WITH option_seed(category_name, points, position, text, correct) AS (
    VALUES
        ('Bībele', 10, 0, '39', FALSE), ('Bībele', 10, 1, '66', TRUE), ('Bībele', 10, 2, '72', FALSE),
        ('Bībele', 30, 0, 'Psalmi', FALSE), ('Bībele', 30, 1, '1. Mozus grāmata', TRUE), ('Bībele', 30, 2, 'Mateja evaņģēlijs', FALSE),
        ('Bībele', 50, 0, 'Vēstulē efeziešiem', TRUE), ('Bībele', 50, 1, 'Atklāsmes grāmatā', FALSE), ('Bībele', 50, 2, 'Rutes grāmatā', FALSE),
        ('Jēzus', 10, 0, 'Nācaretē', FALSE), ('Jēzus', 10, 1, 'Jeruzalemē', FALSE), ('Jēzus', 10, 2, 'Betlēmē', TRUE),
        ('Jēzus', 30, 0, '7', FALSE), ('Jēzus', 30, 1, '10', FALSE), ('Jēzus', 30, 2, '12', TRUE),
        ('Jēzus', 50, 0, '500', FALSE), ('Jēzus', 50, 1, '5000', TRUE), ('Jēzus', 50, 2, '50 000', FALSE),
        ('Jūra', 20, 0, 'Jānis', FALSE), ('Jūra', 20, 1, 'Pēteris', TRUE), ('Jūra', 20, 2, 'Toms', FALSE),
        ('Jūra', 40, 0, 'Liela zivs', TRUE), ('Jūra', 40, 1, 'Delfīns', FALSE), ('Jūra', 40, 2, 'Jūras lauva', FALSE),
        ('Nometne', 10, 0, 'Pateikties Dievam lūgšanā', TRUE), ('Nometne', 10, 1, 'Sacensībā paņemt ēdienu pirmajam', FALSE), ('Nometne', 10, 2, 'Sēdēt klusumā bez iemesla', FALSE),
        ('Nometne', 30, 0, 'Turēt ūdeni tuvumā un klausīt vadītāju', TRUE), ('Nometne', 30, 1, 'Mest ugunī plastmasu', FALSE), ('Nometne', 30, 2, 'Atstāt uguni bez uzraudzības', FALSE),
        ('Nometne', 50, 0, 'Sākt kliegt vēl skaļāk', FALSE), ('Nometne', 50, 1, 'Mierīgi uzklausīt abus un meklēt izlīgumu', TRUE), ('Nometne', 50, 2, 'Izlikties, ka nekas nav noticis', FALSE),
        ('Dziesmas', 10, 0, 'Arfu', TRUE), ('Dziesmas', 10, 1, 'Trompeti', FALSE), ('Dziesmas', 10, 2, 'Bungas', FALSE),
        ('Dziesmas', 30, 0, 'Gavilēt un kalpot ar prieku', TRUE), ('Dziesmas', 30, 1, 'Nekad nedziedāt skaļi', FALSE), ('Dziesmas', 30, 2, 'Dziedāt tikai vienatnē', FALSE),
        ('Dziesmas', 50, 0, 'Lai parādītu, kurš dzied visskaļāk', FALSE), ('Dziesmas', 50, 1, 'Lai pateiktos Dievam un iedrošinātu citus', TRUE), ('Dziesmas', 50, 2, 'Tikai tāpēc, lai aizpildītu laiku', FALSE),
        ('Mīklas', 20, 0, 'Sauls', FALSE), ('Mīklas', 20, 1, 'Goliāts', TRUE), ('Mīklas', 20, 2, 'Nebukadnēcars', FALSE),
        ('Mīklas', 40, 0, 'Rute', FALSE), ('Mīklas', 40, 1, 'Mirjama', FALSE), ('Mīklas', 40, 2, 'Estere', TRUE)
)
INSERT INTO question_options (id, question_id, text, position, correct)
SELECT gen_random_uuid(), q.id, option_seed.text, option_seed.position, option_seed.correct
FROM option_seed
JOIN categories c ON c.name = option_seed.category_name
JOIN camp_template_targets target ON target.game_id = c.game_id
JOIN questions q ON q.category_id = c.id AND q.points = option_seed.points;

UPDATE games
SET description = 'Pilnībā aizpildīta kristīgās nometnes spēle ar 30 jautājumiem, atbildēm un pavedieniem.',
    updated_at = NOW()
WHERE id = '00000000-0000-0000-0000-000000000002';

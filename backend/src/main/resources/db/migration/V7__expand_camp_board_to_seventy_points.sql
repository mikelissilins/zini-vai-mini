ALTER TABLE questions DROP CONSTRAINT questions_points_check;
ALTER TABLE questions ADD CONSTRAINT questions_points_check CHECK (points IN (10, 20, 30, 40, 50, 60, 70));

ALTER TABLE session_questions DROP CONSTRAINT session_questions_points_check;
ALTER TABLE session_questions ADD CONSTRAINT session_questions_points_check CHECK (points IN (10, 20, 30, 40, 50, 60, 70));

WITH seed(category_name, points, question_type, prompt, answer, explanation) AS (
    VALUES
        ('Bībele', 60, 'FREE_TEXT', 'Kuru pravieti Dievs aizveda debesīs ugunīgos ratos?', 'Eliju', 'Pavediens: viņš bija pravieša Elīsas skolotājs.'),
        ('Bībele', 70, 'MULTIPLE_CHOICE', 'Kurā radīšanas dienā Dievs atpūtās?', 'Septītajā dienā', 'Pavediens: pēc sešām radīšanas dienām bija sabats.'),
        ('Jēzus', 60, 'FREE_TEXT', 'Kas kristīja Jēzu Jordānas upē?', 'Jānis Kristītājs', 'Pavediens: viņš dzīvoja tuksnesī un aicināja cilvēkus nožēlot grēkus.'),
        ('Jēzus', 70, 'MULTIPLE_CHOICE', 'Kāds brīnums notika, kad mācekļi vētrā redzēja Jēzu uz jūras?', 'Jēzus gāja pa ūdeni', 'Pavediens: Pēteris uz brīdi arī izkāpa no laivas.'),
        ('Jūra', 60, 'FREE_TEXT', 'Kurš vadīja Israēla tautu cauri Sarkanajai jūrai?', 'Mozus', 'Pavediens: viņš turēja rokā Dieva doto zizli.'),
        ('Jūra', 70, 'MULTIPLE_CHOICE', 'Ko Jona lūdza jūrniekiem darīt, lai vētra rimtos?', 'Iemest viņu jūrā', 'Pavediens: viņš bēga no uzdevuma doties uz Ninivi.'),
        ('Nometne', 60, 'FREE_TEXT', 'Ko darīt, ja nometnes biedrs jūtas viens vai noskumis?', 'Pienākt klāt, uzklausīt un aicināt būt kopā', 'Pavediens: laba komanda pamana arī klusāko biedru.'),
        ('Nometne', 70, 'MULTIPLE_CHOICE', 'Kas ir gudrākais lietus dienas nometnes somā?', 'Lietus jaka un ūdens pudele', 'Pavediens: sausas drēbes un ūdens palīdz vairāk nekā tikai saulesbrilles.'),
        ('Dziesmas', 60, 'FREE_TEXT', 'Kā sauc dziesmu vai lūgšanu krājumu Bībelē, kurā ir 150 psalmi?', 'Psalmi', 'Pavediens: Dāvids sarakstīja daudzus no tiem.'),
        ('Dziesmas', 70, 'MULTIPLE_CHOICE', 'Kas nav slavas dziesmas galvenais mērķis?', 'Uzvarēt karaoke sacensībā', 'Pavediens: slavēšana ir par Dievu, nevis par skaļāko balsi.'),
        ('Mīklas', 60, 'FREE_TEXT', 'Es iztulkoju noslēpumainu rakstu uz ķēniņa pils sienas. Kas es esmu?', 'Daniēls', 'Pavediens: mani iemeta arī lauvu bedrē.'),
        ('Mīklas', 70, 'MULTIPLE_CHOICE', 'Es biju jaunākais no brāļiem, gans un vēlāk ķēniņš. Kas es esmu?', 'Dāvids', 'Pavediens: mans pretinieks bija Goliāts.')
)
INSERT INTO questions (id, category_id, points, question_type, prompt, answer, explanation)
SELECT gen_random_uuid(), c.id, seed.points, seed.question_type, seed.prompt, seed.answer, seed.explanation
FROM seed
JOIN categories c ON c.name = seed.category_name
JOIN games g ON g.id = c.game_id AND g.template_key = 'camp'
ON CONFLICT (category_id, points) DO UPDATE
SET question_type = EXCLUDED.question_type,
    prompt = EXCLUDED.prompt,
    answer = EXCLUDED.answer,
    explanation = EXCLUDED.explanation;

WITH option_seed(category_name, points, position, text, correct) AS (
    VALUES
        ('Bībele', 70, 0, 'Pirmajā pirmdienā', FALSE), ('Bībele', 70, 1, 'Kad Noa pabeidza tīrīt šķirstu', FALSE), ('Bībele', 70, 2, 'Septītajā dienā', TRUE), ('Bībele', 70, 3, 'Tajā dienā, kad tika radīti kartupeļi', FALSE),
        ('Jēzus', 70, 0, 'Jēzus gāja pa ūdeni', TRUE), ('Jēzus', 70, 1, 'Laiva pārvērtās par zemūdeni', FALSE), ('Jēzus', 70, 2, 'Visi mācekļi iemācījās lidot', FALSE), ('Jēzus', 70, 3, 'Jūra uz brīdi kļuva par smiltīm', FALSE),
        ('Jūra', 70, 0, 'Uzlikt burām segu', FALSE), ('Jūra', 70, 1, 'Iemest viņu jūrā', TRUE), ('Jūra', 70, 2, 'Aicināt valsti uz tēju', FALSE), ('Jūra', 70, 3, 'Piesiet vētru pie masta', FALSE),
        ('Nometne', 70, 0, 'Tikai saulesbrilles un konfektes', FALSE), ('Nometne', 70, 1, 'Lietus jaka un ūdens pudele', TRUE), ('Nometne', 70, 2, 'Trīs videospēļu pultis', FALSE), ('Nometne', 70, 3, 'Piepūšams vienradzis bez somas', FALSE),
        ('Dziesmas', 70, 0, 'Pateikties Dievam', FALSE), ('Dziesmas', 70, 1, 'Iedrošināt citus', FALSE), ('Dziesmas', 70, 2, 'Uzvarēt karaoke sacensībā', TRUE), ('Dziesmas', 70, 3, 'Lūgt kopā ar dziesmu', FALSE),
        ('Mīklas', 70, 0, 'Salamans', FALSE), ('Mīklas', 70, 1, 'Dāvids', TRUE), ('Mīklas', 70, 2, 'Noa', FALSE), ('Mīklas', 70, 3, 'Nometnes pavārs ar lingu', FALSE)
)
INSERT INTO question_options (id, question_id, text, position, correct)
SELECT gen_random_uuid(), q.id, option_seed.text, option_seed.position, option_seed.correct
FROM option_seed
JOIN categories c ON c.name = option_seed.category_name
JOIN games g ON g.id = c.game_id AND g.template_key = 'camp'
JOIN questions q ON q.category_id = c.id AND q.points = option_seed.points
ON CONFLICT (question_id, position) DO UPDATE
SET text = EXCLUDED.text,
    correct = EXCLUDED.correct;

INSERT INTO question_options (id, question_id, text, position, correct)
SELECT gen_random_uuid(), q.id,
       CASE c.name
           WHEN 'Bībele' THEN 'Nometnes pingvīns ar Bībeli'
           WHEN 'Jēzus' THEN 'Pazudis kamielis ar karti'
           WHEN 'Jūra' THEN 'Delfīns, kurš vada laivu'
           WHEN 'Nometne' THEN 'Telts, kas pati vada sapulci'
           WHEN 'Dziesmas' THEN 'Mikrofons, kas dzied viens pats'
           ELSE 'Noslēpumains nometnes kaķis'
       END,
       3,
       FALSE
FROM questions q
JOIN categories c ON c.id = q.category_id
JOIN games g ON g.id = c.game_id AND g.template_key = 'camp'
WHERE q.question_type = 'MULTIPLE_CHOICE'
  AND NOT EXISTS (
      SELECT 1 FROM question_options option
      WHERE option.question_id = q.id AND option.position = 3
  );

UPDATE games
SET description = 'Pilnībā aizpildīta kristīgās nometnes spēle ar 42 jautājumiem, atbildēm un pavedieniem.',
    updated_at = NOW()
WHERE template_key = 'camp';

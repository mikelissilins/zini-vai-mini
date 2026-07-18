WITH seed(category_name, points, prompt, answer, explanation) AS (
    VALUES
        ('Bībele', 10, 'Kurš uzbūvēja lielu šķirstu, lai izglābtu ģimeni un dzīvniekus?', 'Noa', 'Pavediens: viņš saņēma ļoti neparastu Dieva būvniecības plānu.'),
        ('Bībele', 20, 'Cik gadus Israēla tauta klejoja tuksnesī?', '40 gadus', 'Pavediens: četrdesmit ir svarīgs skaitlis Bībeles stāstos.'),
        ('Bībele', 30, 'Kurš Israēla soģis lūdza Dievu dot zīmi ar vilnas cirpumu?', 'Gideons', 'Pavediens: vienu reizi rasa bija tikai uz vilnas, otru reizi - tikai apkārt.'),
        ('Bībele', 40, 'Kurš pēc Mozus nāves vadīja Israēla tautu apsolītajā zemē?', 'Jozua', 'Pavediens: viņš kopā ar Kālebu bija viens no diviem uzticīgajiem izlūkiem.'),
        ('Bībele', 50, 'Kurā Bībeles grāmatā ir stāsts par Ruti un viņas vīramāti Naomiju?', 'Rutes grāmata', 'Pavediens: šī grāmata ir nosaukta galvenās varones vārdā.'),
        ('Bībele', 60, 'Kurš pravietis sacīja ķēniņam Dāvidam: “Tu esi tas vīrs” pēc stāsta par jēriņu?', 'Nātāns', 'Pavediens: viņš Dāvidam izstāstīja līdzību par bagāto vīru un nabaga jēriņu.'),
        ('Bībele', 70, 'Kurš Jūdas ķēniņš kļuva par ķēniņu astoņu gadu vecumā un vēlāk atrada bauslības grāmatu templī?', 'Josija', 'Pavediens: viņa valdīšanas laikā tauta atjaunoja derību ar Dievu.'),

        ('Jēzus', 10, 'Kur piedzima Jēzus?', 'Betlēmē', 'Pavediens: gani Viņu atrada silītē.'),
        ('Jēzus', 20, 'Kā sauca Jēzus māti?', 'Marija', 'Pavediens: eņģelis Gabriēls viņai nesa priecīgu ziņu.'),
        ('Jēzus', 30, 'Kurā evaņģēlijā vienīgajā ir līdzība par žēlsirdīgo samarieti?', 'Lūkas evaņģēlijā', 'Pavediens: šajā evaņģēlijā ir arī līdzība par pazudušo dēlu.'),
        ('Jēzus', 40, 'Cik akmens traukus ar ūdeni Jēzus lika piepildīt kāzās Kānā?', 'Sešus', 'Pavediens: pēc tam ūdens kļuva par vīnu.'),
        ('Jēzus', 50, 'Kurā ciemā dzīvoja Marta, Marija un Lācars?', 'Bētānijā', 'Pavediens: ciems bija netālu no Jeruzalemes.'),
        ('Jēzus', 60, 'Kā sauca augstā priestera kalpu, kuram Pēteris nocirta ausi?', 'Malhs', 'Pavediens: Jēzus viņu dziedināja Ģetzemanes dārzā.'),
        ('Jēzus', 70, 'Kādus aramiešu vārdus Jēzus sacīja Jaiŗa meitai, kad viņu uzcēla?', 'Talita kūmi', 'Pavediens: Marka evaņģēlijs šo frāzi saglabā oriģinālvalodā.'),

        ('Matemātika', 10, 'Cik ir 2 + 2?', '4', 'Pavediens: pat nometnes pavārs šo sarēķina pirms kafijas.'),
        ('Matemātika', 20, 'Cik ir 6 × 7?', '42', 'Pavediens: šis skaitlis ir arī atbilde uz dzīvi, Visumu un visu pārējo kādā slavenā grāmatā.'),
        ('Matemātika', 30, 'Cik ir 3/4 no 48?', '36', 'Pavediens: vispirms atrodi vienu ceturtdaļu, pēc tam reizini ar trīs.'),
        ('Matemātika', 40, 'Cena pieaug no 120 līdz 150 eiro. Par cik procentiem tā pieaug?', '25%', 'Pavediens: pieaugums ir 30; salīdzini to ar sākuma cenu 120.'),
        ('Matemātika', 50, 'Taisnstūrim malas ir 7 cm un 9 cm. Kāds ir tā perimetrs?', '32 cm', 'Pavediens: saskaiti visas četras malas, nevis tikai laukumu.'),
        ('Matemātika', 60, 'Kāda ir divpadsmitstūra iekšējo leņķu summa?', '1800 grādi', 'Pavediens: izmanto formulu (n − 2) × 180.'),
        ('Matemātika', 70, 'Cik dažādu kvadrātu kopā ir parastā 8 × 8 šaha dēlī?', '204', 'Pavediens: jāsaskaita 1×1, 2×2 un visi pārējie līdz 8×8 kvadrāti.'),

        ('Jūra', 10, 'Kāda ir Baltijas jūras ūdens garša?', 'Sāļa', 'Pavediens: lūdzu, nepārbaudi to ar pilnu muti.'),
        ('Jūra', 20, 'Kā sauc cilvēku, kurš vada kuģi?', 'Kapteinis', 'Pavediens: viņš uz kuģa ir atbildīgs par kursu un apkalpi.'),
        ('Jūra', 30, 'Kāpēc Baltijas jūras ūdens ir mazāk sāļš nekā okeānā?', 'Tajā sajaucas upju saldūdens ar sāļo jūras ūdeni', 'Pavediens: Baltijas jūrai ir šaura saikne ar okeānu, bet tajā ietek daudz upju.'),
        ('Jūra', 40, 'Cik grādu ir Griničas meridiāna ģeogrāfiskais garums?', '0 grādi', 'Pavediens: no šīs līnijas mēra austrumu un rietumu garumu.'),
        ('Jūra', 50, 'Kuru instrumentu jūrnieks izmanto, lai mērītu gaisa spiedienu un pamanītu laikapstākļu maiņu?', 'Barometru', 'Pavediens: krītošs spiediens bieži sola sliktāku laiku.'),
        ('Jūra', 60, 'Kura silta okeāna straume būtiski ietekmē Rietumeiropas maigāko klimatu?', 'Golfa straume', 'Pavediens: tā nes siltu ūdeni pāri Atlantijas okeānam.'),
        ('Jūra', 70, 'Kāds ir galvenais plūdmaiņu cēlonis?', 'Mēness gravitācijas pievilkšanās', 'Pavediens: Saule arī ietekmē, bet Mēness ir galvenais spēlētājs.'),

        ('Nometne', 10, 'Ko dara laba komanda, ja pārgājienā kāds atpaliek?', 'Apstājas un paliek kopā', 'Pavediens: nevienu neatstāj aiz muguras.'),
        ('Nometne', 20, 'Nosauc divas lietas, kas noder lietainā nometnes dienā.', 'Lietus jaka un ūdensizturīgi apavi', 'Pavediens: saulesbrilles vien šoreiz neizglābs.'),
        ('Nometne', 30, 'Kartes mērogs ir 1 : 25 000. Cik kilometru dabā ir 4 cm kartē?', '1 kilometrs', 'Pavediens: 1 cm kartē ir 25 000 cm jeb 250 m dabā.'),
        ('Nometne', 40, 'Nosauc trīs lietas, kas vajadzīgas uguns trijstūrim.', 'Siltums, degviela un skābeklis', 'Pavediens: ja atņem vienu no trim, uguns nevar turpināt degt.'),
        ('Nometne', 50, 'Ko visdrošāk darīt, ja pārgājienā sākas pērkona negaiss?', 'Doties uz drošu ēku vai slēgtu transportlīdzekli un izvairīties no atklātas vietas', 'Pavediens: ūdens, vientuļi koki un atklāti lauki nav droša izvēle.'),
        ('Nometne', 60, 'Kādu kompasa virzienu parasti apzīmē ar 0° vai 360°?', 'Ziemeļus', 'Pavediens: sarkanā adatas puse rāda šo virzienu.'),
        ('Nometne', 70, 'Pārgājiens sākas 8.45. Iešana ilgst 1 h 35 min, tad ir 20 min pauze un vēl 50 min iešana. Cikos komanda ierodas?', '11.30', 'Pavediens: saskaiti 1:35 + 0:20 + 0:50, tad pieskaiti sākuma laikam.'),

        ('Jautrās mīklas', 10, 'Kas kļūst slapjāks, jo vairāk tas žāvē?', 'Dvielis', 'Pavediens: tas noteikti nav lietussargs ar sliktiem nodomiem.'),
        ('Jautrās mīklas', 20, 'Kas var piepildīt visu istabu, bet neaizņem vietu?', 'Gaisma', 'Pavediens: ieslēdz lampu un skaties.'),
        ('Jautrās mīklas', 30, 'Ir trīs kastes ar uzrakstiem “āboli”, “apelsīni” un “jaukti”. Visi uzraksti ir nepareizi. No kuras kastes jāizvelk viens auglis, lai varētu pareizi pārrakstīt visas?', 'No kastes ar uzrakstu “jaukti”', 'Pavediens: tā noteikti nevar būt jaukta, jo visi uzraksti ir nepareizi.'),
        ('Jautrās mīklas', 40, 'Es esmu izrakts no zemes, ieslēgts kokā un nekad netieku izlaists, tomēr mani lieto gandrīz visi. Kas es esmu?', 'Zīmuļa grafīts', 'Pavediens: zīmulis nav cietums, bet šis materiāls tajā dzīvo.'),
        ('Jautrās mīklas', 50, 'Tu apdzen skrējēju, kas ir otrajā vietā. Kurā vietā esi tagad?', 'Otrajā vietā', 'Pavediens: tu pārņēmi viņa vietu, nevis pirmo vietu.'),
        ('Jautrās mīklas', 60, 'Divi tēvi un divi dēli noķer trīs zivis, un katrs saņem pa vienai. Kā tas iespējams?', 'Tie ir vectēvs, tēvs un dēls', 'Pavediens: cilvēku ir tikai trīs, bet lomām ir pārklāšanās.'),
        ('Jautrās mīklas', 70, 'Citā istabā ir viena spuldze, bet tavā istabā trīs slēdži. Spuldzi drīksti apskatīt tikai vienu reizi. Kā noskaidrot, kurš slēdzis to ieslēdz?', 'Ieslēgt pirmo, pagaidīt, izslēgt to un ieslēgt otro; pie spuldzes pārbaudīt, vai tā deg, ir silta vai auksta', 'Pavediens: izmanto ne tikai gaismu, bet arī spuldzes siltumu.')
)
UPDATE questions q
SET prompt = seed.prompt,
    answer = seed.answer,
    explanation = seed.explanation
FROM categories c
JOIN games g ON g.id = c.game_id AND g.template_key = 'camp-challenge'
JOIN seed ON seed.category_name = c.name
WHERE q.category_id = c.id
  AND q.points = seed.points;

WITH option_seed(category_name, points, position, text, correct) AS (
    VALUES
        ('Bībele', 10, 0, 'Noa', TRUE), ('Bībele', 10, 1, 'Mozus', FALSE), ('Bībele', 10, 2, 'Ābrahāms', FALSE), ('Bībele', 10, 3, 'Jāzeps', FALSE),
        ('Bībele', 50, 0, 'Soģu grāmata', FALSE), ('Bībele', 50, 1, 'Rutes grāmata', TRUE), ('Bībele', 50, 2, 'Esteres grāmata', FALSE), ('Bībele', 50, 3, 'Sakāmvārdu grāmata', FALSE),
        ('Jēzus', 10, 0, 'Nācaretē', FALSE), ('Jēzus', 10, 1, 'Betlēmē', TRUE), ('Jēzus', 10, 2, 'Jeruzalemē', FALSE), ('Jēzus', 10, 3, 'Kapernaumā', FALSE),
        ('Jēzus', 50, 0, 'Bētānijā', TRUE), ('Jēzus', 50, 1, 'Kapernaumā', FALSE), ('Jēzus', 50, 2, 'Betsaidā', FALSE), ('Jēzus', 50, 3, 'Nācaretē', FALSE),
        ('Matemātika', 10, 0, '3', FALSE), ('Matemātika', 10, 1, '4', TRUE), ('Matemātika', 10, 2, '5', FALSE), ('Matemātika', 10, 3, '6', FALSE),
        ('Matemātika', 50, 0, '16 cm', FALSE), ('Matemātika', 50, 1, '32 cm', TRUE), ('Matemātika', 50, 2, '63 cm', FALSE), ('Matemātika', 50, 3, '126 cm', FALSE),
        ('Jūra', 10, 0, 'Sāļa', TRUE), ('Jūra', 10, 1, 'Saldūdens', FALSE), ('Jūra', 10, 2, 'Salda', FALSE), ('Jūra', 10, 3, 'Bez garšas', FALSE),
        ('Jūra', 50, 0, 'Higrometru', FALSE), ('Jūra', 50, 1, 'Anemometru', FALSE), ('Jūra', 50, 2, 'Barometru', TRUE), ('Jūra', 50, 3, 'Termometru', FALSE),
        ('Nometne', 10, 0, 'Skrien tālāk, lai uzvarētu', FALSE), ('Nometne', 10, 1, 'Apstājas un paliek kopā', TRUE), ('Nometne', 10, 2, 'Atstāj zīmi un gaida finišā', FALSE), ('Nometne', 10, 3, 'Sazvana vadītāju, bet turpina ceļu', FALSE),
        ('Nometne', 50, 0, 'Paslēpties zem vientuļa koka', FALSE), ('Nometne', 50, 1, 'Turpināt peldēt līdz krastam', FALSE), ('Nometne', 50, 2, 'Doties uz drošu ēku vai transportlīdzekli', TRUE), ('Nometne', 50, 3, 'Apgulties atklātā pļavā', FALSE),
        ('Jautrās mīklas', 10, 0, 'Dvielis', TRUE), ('Jautrās mīklas', 10, 1, 'Sūklis', FALSE), ('Jautrās mīklas', 10, 2, 'Lupata', FALSE), ('Jautrās mīklas', 10, 3, 'Lietussargs', FALSE),
        ('Jautrās mīklas', 50, 0, 'Pirmajā vietā', FALSE), ('Jautrās mīklas', 50, 1, 'Otrajā vietā', TRUE), ('Jautrās mīklas', 50, 2, 'Trešajā vietā', FALSE), ('Jautrās mīklas', 50, 3, 'Finišā', FALSE)
)
UPDATE question_options option
SET text = option_seed.text,
    correct = option_seed.correct
FROM questions q, categories c, games g, option_seed
WHERE option.question_id = q.id
  AND c.id = q.category_id
  AND g.id = c.game_id
  AND g.template_key = 'camp-challenge'
  AND option_seed.category_name = c.name
  AND option_seed.points = q.points
  AND option_seed.position = option.position;

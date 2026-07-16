INSERT INTO games (id, title, description, locale, template, template_key, version, created_at, updated_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Tukša spēle', 'Sāc no baltas lapas un izveido savas sadaļas.', 'lv', TRUE, 'blank', 0, NOW(), NOW()),
    ('00000000-0000-0000-0000-000000000002', 'Zini vai mini – nometnes spēle', 'Sešas nometnes tēmas ar 10–50 punktu jautājumu vietām.', 'lv', TRUE, 'camp', 0, NOW(), NOW());

DO $$
DECLARE
    category_names TEXT[] := ARRAY['Bībele', 'Jēzus', 'Jūra', 'Nometne', 'Dziesmas', 'Mīklas'];
    category_colors TEXT[] := ARRAY['#0E758C', '#F77F5B', '#55B8CC', '#5CA67A', '#7A6FF0', '#F2A65A'];
    category_id UUID;
    category_index INTEGER;
    point_value INTEGER;
BEGIN
    FOR category_index IN 1..array_length(category_names, 1) LOOP
        category_id := gen_random_uuid();
        INSERT INTO categories (id, game_id, name, color, position)
        VALUES (category_id, '00000000-0000-0000-0000-000000000002', category_names[category_index], category_colors[category_index], category_index - 1);

        FOREACH point_value IN ARRAY ARRAY[10, 20, 30, 40, 50] LOOP
            INSERT INTO questions (id, category_id, points, question_type, prompt, answer)
            VALUES (gen_random_uuid(), category_id, point_value, 'FREE_TEXT', '', '');
        END LOOP;
    END LOOP;
END $$;

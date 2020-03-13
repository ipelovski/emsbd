package emsbj.generation;

import java.util.LinkedHashMap;

class TeachingProgram extends LinkedHashMap<String, Integer> {
    static final String lit = "Български език и литература";
    static final String ang = "Английски език";
    static final String mat = "Математика";
    static final String it = "Информационни технологии";
    static final String fiz = "Физика и астрономия";
    static final String bio = "Биология и здравно образование";
    static final String him = "Химия и опазване на околната среда";
    static final String ist = "История и цивилизации";
    static final String geo = "География и икономика";
    static final String fil = "Философия";
    static final String izo = "Изобразително изкуство";
    static final String muz = "Музика";
    static final String fizvuz = "Физическо възпитание и спорт";
    static final String teh = "Технологии и предприемачество";
    static final String gra = "Гражданско образование";

    TeachingProgram() {
        super();
    }

    TeachingProgram(TeachingProgram teachingProgram) {
        super(teachingProgram);
    }

    static TeachingProgram create9() {
        TeachingProgram program = new TeachingProgram();
        program.put(lit, 3);
        program.put(ang, 2);
        program.put(mat, 3);
        program.put(it, 2);
        program.put(fiz, 2);
        program.put(bio, 2);
        program.put(him, 2);
        program.put(ist, 2);
        program.put(geo, 2);
        program.put(fil, 2);
        program.put(izo, 2);
        program.put(muz, 2);
        program.put(fizvuz, 2);
        program.put(teh, 2);
        return program;
    }

    static TeachingProgram create10() {
        TeachingProgram program = new TeachingProgram();
        program.put(lit, 3);
        program.put(ang, 3);
        program.put(mat, 2);
        program.put(it, 2);
        program.put(fiz, 2);
        program.put(bio, 2);
        program.put(him, 2);
        program.put(ist, 2);
        program.put(geo, 2);
        program.put(fil, 2);
        program.put(izo, 2);
        program.put(muz, 2);
        program.put(fizvuz, 2);
        program.put(teh, 2);
        return program;
    }

    static TeachingProgram create11() {
        TeachingProgram program = new TeachingProgram();
        program.put(lit, 3);
        program.put(ang, 3);
        program.put(mat, 2);
        program.put(it, 2);
        program.put(fiz, 2);
        program.put(bio, 2);
        program.put(him, 2);
        program.put(ist, 2);
        program.put(geo, 2);
        program.put(fil, 2);
        program.put(izo, 2);
        program.put(muz, 2);
        program.put(fizvuz, 2);
        program.put(teh, 2);
        program.put(gra, 1);
        return program;
    }

    static TeachingProgram create12() {
        return create11();
    }
}

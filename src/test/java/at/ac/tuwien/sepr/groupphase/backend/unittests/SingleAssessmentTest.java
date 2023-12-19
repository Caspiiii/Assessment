package at.ac.tuwien.sepr.groupphase.backend.unittests;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.NeuroTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.ProcessTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.PsychoTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.NeuroTrait;
import at.ac.tuwien.sepr.groupphase.backend.entity.ProcessTrait;
import at.ac.tuwien.sepr.groupphase.backend.entity.PsychoTrait;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.entity.Trait;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.AnswerRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TraitRepositroy;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.algorithms.SingleAssessment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class SingleAssessmentTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepositoryInterface userRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    SingleAssessment singleAssessment;

    @Autowired
    TraitRepositroy traitRepositroy;

    private final ApplicationUser applicationUser1 = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(-1L)
        .withFirstName("Mark")
        .withLastName("Wahlberg")
        .withEmail("mark@email.com")
        .withPassword("password")
        .withRole(Role.USER)
        .build();
    private final ApplicationUser applicationUser2 = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(-2L)
        .withFirstName("Brad")
        .withLastName("Pitt")
        .withEmail("bradk@email.com")
        .withPassword("password")
        .withRole(Role.USER)
        .build();
    private final ApplicationUser applicationUser3 = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(-3L)
        .withFirstName("Leonardo")
        .withLastName("Dicaprio")
        .withEmail("leonardo@email.com")
        .withPassword("password")
        .withRole(Role.MANAGER)
        .build();

    @BeforeEach
    public void generateQuestions() throws SQLException {
        Connection c = dataSource.getConnection();
        Statement s = c.createStatement();
        //s.executeUpdate("DROP ALL OBJECTS");

        // Disable FK
        s.execute("SET REFERENTIAL_INTEGRITY FALSE");

        // Find all tables and truncate them
        Set<String> tables = new HashSet<>();
        ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        for (String table : tables) {
            s.executeUpdate("DELETE FROM " + table);
        }
        // Enable FK
        s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        s.close();
        c.close();

        userRepository.save(applicationUser1);
        userRepository.save(applicationUser2);
        userRepository.save(applicationUser3);

        final PsychoTrait innereSicherheit = new PsychoTrait("Innere Sicherheit", "Fähigkeit, die eigenen Bedürfnisse warzunehmen und darauf zu vertrauen, dass auch umliegende Menschen diese beachten", 3, 10);
        final PsychoTrait gefuehlsWahrnehmung = new PsychoTrait("Gefühlswahrnehmung", "Fähigkeit, die Balance zwischen der Nutzung des Verstandes und Einbindung des Unterbewussten halten zu können", 3, 10);
        final PsychoTrait feedbackVerwertung = new PsychoTrait("Feedbackverwertung", "Fähigkeit, die Folgen der eigenen Handlung auch unterbewusst verarbeiten zu können", 3, 10);
        final PsychoTrait parallelVerarbeitung = new PsychoTrait("Parallelverarbeitung", "Fähigkeit, parallel mehrere Aspekte (auch unter Druck) im Zugriff zu haben", 3, 10);
        final PsychoTrait zugangUnbewussten = new PsychoTrait("Zugang zum Unbewussten", "Fähigkeit, mit unterbewussten Efahrungen umgehen zu können", 3, 10);
        final PsychoTrait wachsamkeit = new PsychoTrait("Wachsamkeit", "Fähigkeit, die Bedürfnisse anderer zu beachten ohne die eigenen zu übergehen", 3, 10);
        final PsychoTrait affektRegulation = new PsychoTrait("Affektregulation", "Fähigkeit, die eigenen Affekte genauer beobachten zu können", 3, 10);
        List<PsychoTrait> iskgpvw = new ArrayList<>();
        iskgpvw.add(innereSicherheit);
        iskgpvw.add(gefuehlsWahrnehmung);
        iskgpvw.add(parallelVerarbeitung);
        iskgpvw.add(wachsamkeit);
        List<PsychoTrait> ausfv = new ArrayList<>();
        ausfv.add(affektRegulation);
        ausfv.add(feedbackVerwertung);
        ausfv.add(zugangUnbewussten);
        List<PsychoTrait> iskgfvpv = new ArrayList<>();
        iskgfvpv.add(innereSicherheit);
        iskgfvpv.add(gefuehlsWahrnehmung);
        iskgfvpv.add(parallelVerarbeitung);
        iskgfvpv.add(feedbackVerwertung);
        List<PsychoTrait> fvausw = new ArrayList<>();
        fvausw.add(affektRegulation);
        fvausw.add(feedbackVerwertung);
        fvausw.add(zugangUnbewussten);
        fvausw.add(wachsamkeit);
        final Trait entdeckergeist = new ProcessTrait("Entdeckergeist", "Überdurschnittliche Fähigkeit, Veränderungen im Markt aufzugreifen.", 1, 30, iskgpvw);
        final ProcessTrait erfindungsgabe = new ProcessTrait("Erfindungsgabe", "Überdurschnittliche Fähigkeit, Ideen als Antwort für Veränderungen zu finden", 1, 30, ausfv);
        final ProcessTrait urteilsvermoegen = new ProcessTrait("Urteilsvermögen", "Überdurschnittliche Fähigkeit, mögliche Lösung auf Machbarkeit und Zukunftsfähigkeit zu analysieren", 1, 30, iskgfvpv);
        final ProcessTrait begeisterung = new ProcessTrait("Begeisterung", "Überdurschnittliche Fähigkeit, andere für eine Lösung zu gewinnen und zu motivieren", 1, 30, ausfv);
        final ProcessTrait befaehigung = new ProcessTrait("Befähigung", "Überdurschnittliche Fähigkeit, Skills, Fertigkeiten und Wissen zu vermitteln um Lösung umsetzen zu können", 1, 30, iskgfvpv);
        final ProcessTrait beharrlichkeit = new ProcessTrait("Beharrlichkeit", "Überdurschnittliche Fähigkeit, Konsequenz zu zeigen und auch bei Schwierigkeiten durchzuhalten", 1, 30, fvausw);
        final NeuroTrait intentionsgedaechtnis = new NeuroTrait("Intentionsgedächtnis", "Überdurschnittliche Fähigkeit, rational zu arbeiten und überlegt vorzugehen ", 2, 10, iskgpvw);
        final NeuroTrait objekterkennungssystem = new NeuroTrait("Objekterkennungssystem", "Überdurschnittliche Fähigkeit, Fehler zu erkennen und Gründlichkeit auszuüben", 2, 10, iskgfvpv);
        final NeuroTrait extensionsgedaechtnis = new NeuroTrait("Extensionsgedächtnis", "Überdurschnittliche Fähigkeit, mit Stress umzugehen und Kreativität zu zeigen", 2, 10, fvausw);
        final NeuroTrait intuitiveVerhaltenssteuerung = new NeuroTrait("Intuitive Verhaltenssteuerung", "Überdurschnittliche Fähigkeit, Spontanität zu zeigen und sich selbst zu motivieren", 2, 10, ausfv);

        traitRepositroy.save(innereSicherheit);
        traitRepositroy.save(gefuehlsWahrnehmung);
        traitRepositroy.save(parallelVerarbeitung);
        traitRepositroy.save(wachsamkeit);
        traitRepositroy.save(affektRegulation);
        traitRepositroy.save(feedbackVerwertung);
        traitRepositroy.save(zugangUnbewussten);
        traitRepositroy.save(entdeckergeist);
        traitRepositroy.save(erfindungsgabe);
        traitRepositroy.save(urteilsvermoegen);
        traitRepositroy.save(begeisterung);
        traitRepositroy.save(befaehigung);
        traitRepositroy.save(beharrlichkeit);
        traitRepositroy.save(intentionsgedaechtnis);
        traitRepositroy.save(objekterkennungssystem);
        traitRepositroy.save(extensionsgedaechtnis);
        traitRepositroy.save(intuitiveVerhaltenssteuerung);

        questionRepository.save(new Question("Ich fühle mich wohl, wenn ich länger sitze und über die Bedeutung der Dinge nachdenke als die meisten anderen.", 1, entdeckergeist));
        questionRepository.save(new Question("Ich kann nicht anders, als neue und originelle Ideen zu entwickeln, auch wenn es nicht notwendig ist. ", 1, erfindungsgabe));
        questionRepository.save(new Question("Andere sagen ich habe ein gutes Gespür für die Lage oder Situation in der wir sind.", 1, urteilsvermoegen));
        questionRepository.save(new Question("Ich habe die Gabe, Menschen für einen Plan oder eine Idee zu gewinnen und sie zum Handeln zu inspirieren.", 1, begeisterung));
        questionRepository.save(new Question("Menschen, die mich kennen, würden sagen, dass ich auf natürliche Weise auf die Bedürfnisse anderer eingehen kann", 1, befaehigung));
        questionRepository.save(new Question("Ich erhalte mehr Befriedigung und Erfüllung als die meisten Menschen, wenn ich ein Projekt bis zum Ende durchziehe.", 1, beharrlichkeit));

        questionRepository.save(new Question("Ich verbringe mehr Zeit als die meisten Menschen damit, über Probleme in der Welt um mich herum nachzudenken.", 1, entdeckergeist));
        questionRepository.save(new Question("Die Leute sagen, dass ich nicht aufhören kann, innovativ zu sein oder neue Ideen vorzuschlagen.", 1, erfindungsgabe));
        questionRepository.save(new Question("Ich bin viel besser als die meisten Menschen in der Lage, mein Bauchgefühl zu nutzen, wenn wenig Daten oder Informationen verfügbar sind.", 1, urteilsvermoegen));
        questionRepository.save(new Question("Andere sagen, ich habe ein Händchen dafür, Menschen davon zu überzeugen, sich auf Projekte und Ideen einzulassen und diese zu unterstützen. ", 1, begeisterung));
        questionRepository.save(new Question("Im Vergleich zu anderen bin ich besonders traurig und enttäuscht, wenn ich unerfülltes Potenzial bei Menschen oder meinem Umfeld sehe.", 1, befaehigung));
        questionRepository.save(new Question("Andere würden sagen, dass ich unerbittlich bin, was meine Aufmerksamkeit für Details und die Erreichung von Zielen angeht. ", 1, beharrlichkeit));

        questionRepository.save(new Question("Ich halte mich für einen idealistischen Träumer. Ich bin einzigartig begabt darin, subtile Nuancen, Trends und Muster zu erkennen, die den meisten anderen entgehen.", 1, entdeckergeist));
        questionRepository.save(new Question("Ich bekomme Energie, wenn ich aus dem Nichts etwas Neues erschaffen muss.", 1, erfindungsgabe));
        questionRepository.save(new Question("Andere würden sagen, dass ich bessere Instinkte und Intuition habe als die meisten Menschen.", 1, urteilsvermoegen));
        questionRepository.save(new Question("Andere würden sagen, dass ich Menschen schnell und enthusiastisch für neue Ideen gewinne.", 1, begeisterung));
        questionRepository.save(new Question("Im Vergleich zu den meisten anderen bin ich eine äußerst reaktionsschnelle und hilfsbereite Person. ", 1, befaehigung));
        questionRepository.save(new Question("Ich verpasse selten eine Frist oder ein Ziel und könnte mir nicht vorstellen, dies zuzulassen.", 1, beharrlichkeit));

        questionRepository.save(new Question("Menschen, die mich gut kennen, würden sagen, dass ich mich oft in meinen eigenen Gedanken verliere.", 1, entdeckergeist));
        questionRepository.save(new Question("Ich habe eine einzigartige und unbestreitbare Fähigkeit, die zugrundeliegende Essenz jeder Situation oder Herausforderung zu erkennen.", 1, erfindungsgabe));
        questionRepository.save(new Question("Ich bin sehr begabt darin, zu sehen, wie emotionale Faktoren in Entscheidungen und Situationen integriert werden müssen.", 1, urteilsvermoegen));
        questionRepository.save(new Question("Mir macht es  großen Spaß, andere für eine neue Idee oder Möglichkeit zu begeistern.", 1, begeisterung));
        questionRepository.save(new Question("Service und Gastfreundschaft sind ein großer Teil meiner Identität.", 1, befaehigung));
        questionRepository.save(new Question("Im Vergleich zu den meisten Menschen macht es mir wirklich Spaß, ein Projekt oder eine Aufgabe bis zum Ende durchzuziehen.", 1, beharrlichkeit));

        questionRepository.save(new Question("Ich frage mich mehr als die meisten anderen, warum die Dinge so sind, wie sie sind.", 1, entdeckergeist));
        questionRepository.save(new Question("Andere halten mich für einen extrem innovativen Menschen.", 1, erfindungsgabe));
        questionRepository.save(new Question("Die Leute sagen, dass ich eine unheimliche Fähigkeit habe, eine Idee zu beurteilen oder zu bewerten, auch ohne umfangreiche Details oder Informationen.", 1, urteilsvermoegen));
        questionRepository.save(new Question("Im Vergleich zu den meisten anderen bin ich gut darin, Menschen zu rekrutieren und sie dazu zu bringen, sich zu bewegen.", 1, begeisterung));
        questionRepository.save(new Question("Ich kann nicht anders, als mich in die Menschen hineinzuversetzen und sie bei allem zu unterstützen, was sie brauchen.", 1, befaehigung));
        questionRepository.save(new Question("Wenn sich die Dynamik und der Fortschritt verlangsamen, bin ich gerne derjenige, der die Leute wieder anspornt, weiterzumachen.", 1, beharrlichkeit));

        questionRepository.save(new Question("Ein origineller und kreativer Denker zu sein, ist ein großer Teil meiner Identität.", 1, entdeckergeist));
        questionRepository.save(new Question("Ich ziehe es vor, etwas zu schaffen, indem ich von einem \"weissen Papier\" arbeite, anstatt etwas zu optimieren, das bereits etabliert ist.", 1, erfindungsgabe));
        questionRepository.save(new Question("Ich habe die Fähigkeit, Muster zu erkennen. Das Gespür  wie Teile zusammenhängen und gute Einschätzungsgabe der Machbarkeit ", 1, urteilsvermoegen));
        questionRepository.save(new Question("Ich kann Dinge sei es Projekte oder Aufgaben in Bewegung bringen. ", 1, begeisterung));
        questionRepository.save(new Question("Ich habe das Gefühl, dass ich die erste Person bin, die die Leute fragen, ob sie sich freiwillig engagieren möchte, weil sie wissen, dass ich fast immer Ja sagen werde.", 1, befaehigung));
        questionRepository.save(new Question("Ich bekomme Energie, wenn ich eine Liste mit konkreten Aufgaben vor mir habe.", 1, beharrlichkeit));


        questionRepository.save(new Question("Wenn mir die Energie zum Handeln abhanden kommt, weil zu viel ansteht, dann kann ich mich nicht gut wieder aufraffen. ", 2, intentionsgedaechtnis));
        questionRepository.save(new Question("Wenn mich einige Rückschläge richtig runterziehen, dann komme ich nicht so schnell aus eigener Kraft auf die Beine. ", 2, objekterkennungssystem));
        questionRepository.save(new Question("Wenn ich einmal ganz spontan und impulsiv gelaunt bin, dann fällt es mir schwer, wieder etwas besonnener vorzugehen. ", 2, intuitiveVerhaltenssteuerung));
        questionRepository.save(new Question("Es liegt mir nicht, mich mit meinen Schwächen auseinanderzusetzen.  ", 2, extensionsgedaechtnis));
        questionRepository.save(new Question("Wenn ich einige schwierige Aufgaben zu erledigen habe, weiche ich gern auf leichtere Aktivitäten aus. ", 2, intentionsgedaechtnis));
        questionRepository.save(new Question("Wenn ich etwas sehr Schmerzhaftes erlebt habe, dann kann ich mich für einige Zeit auf nichts anderes konzentrieren.  ", 2, objekterkennungssystem));
        questionRepository.save(new Question("Ich handle meist spontan aus dem Moment heraus. ", 2, intuitiveVerhaltenssteuerung));
        questionRepository.save(new Question("Sich mit leidvollen Erfahrungen tiefer gehend auseinanderzusetzen, liegt mir nicht. ", 2, extensionsgedaechtnis));
        questionRepository.save(new Question("Wenn ich an meine unerledigten Vorsätze denke, dann zieht mich das ziemlich runter.", 2, intentionsgedaechtnis));
        questionRepository.save(new Question("Wenn mir etwas Angst macht, komme ich davon aus eigener Kraft nicht so schnell wieder los. ", 2, objekterkennungssystem));
        questionRepository.save(new Question("Ich handle oft auch in Situationen sehr spontan, in denen es besser wäre, unterschiedliche Gesichtspunkte abzuwägen. ", 2, intuitiveVerhaltenssteuerung));
        questionRepository.save(new Question("Ich verdränge negative Gefühle auch dann, wenn es eigentlich besser wäre, ihnen nachzuspüren.  ", 2, extensionsgedaechtnis));


        questionRepository.save(new Question("Auch wenn meine Stimmung noch so tief sinkt, spüre ich in mir immer eine Kraft, die mich früher oder später wieder nach oben bringt. ", 3, true, innereSicherheit));
        questionRepository.save(new Question("Wenn mich etwas persönlich angeht, dann ist das immer von deutlich spürbaren Gefühlen begleitet. ", 3, true, gefuehlsWahrnehmung));
        questionRepository.save(new Question("Mir passiert es öfters, dass ich denselben Fehler wieder mache. ", 3, feedbackVerwertung));
        questionRepository.save(
            new Question("Wenn ich eine schwierige Entscheidung getroffen habe, dann spüre ich meist, dass ich irgendwie alles Wichtige berücksichtigt habe, auch ohne über jeden Punkt einzeln nachgedacht zu haben. ", 3, true,
                parallelVerarbeitung));
        questionRepository.save(new Question("Die bewusste Kontrolle loszulassen, fällt mir schwer. ", 3, zugangUnbewussten));
        questionRepository.save(new Question("Wenn ich einen Vorsatz gefasst habe, dann übersehe ich oft Gelegenheiten, wo ich ihn hätte umsetzen können. ", 3, wachsamkeit));
        questionRepository.save(new Question("Ungute Gefühle kann ich ganz gut dadurch regulieren, dass ich sie zuerst einmal an mich heranlasse. ", 3, true, affektRegulation));
        questionRepository.save(new Question("Ich habe immer ein sicheres Gespür dafür, was mir gut tut und was nicht.  ", 3, true, innereSicherheit));
        questionRepository.save(new Question("Bei wichtigen Entscheidungen lasse ich subjektive Gefühle außen vor. ", 3, gefuehlsWahrnehmung));
        questionRepository.save(new Question("Eigene Fehler kann ich immer ganz gut nutzen, um etwas dazu zu lernen. ", 3, true, feedbackVerwertung));
        questionRepository.save(new Question("Wenn ich einen Menschen mag, dann übersehe ich allzu leicht seine negativen Seiten. ", 3, parallelVerarbeitung));
        questionRepository.save(new Question("Symbole in Märchen oder in Träumen verstehe ich oft ganz intuitiv. ", 3, true, zugangUnbewussten));
        questionRepository.save(new Question("Meine Aufmerksamkeit wird oft durch nebensächliche Dinge abgelenkt. ", 3, wachsamkeit));
        questionRepository.save(new Question("Ich komme mit unguten Erfahrungen am besten klar, wenn ich sie in einer ruhigen Stunde an mich heranlasse. ", 3, true, affektRegulation));
        questionRepository.save(new Question("Ich möchte lernen, ein besseres Gespür für alle meine Bedürfnisse zu bekommen. ", 3, innereSicherheit));
        questionRepository.save(new Question("Ich wünschte mir, dass mir meine Gefühle sagen, welche Entscheidung richtig ist. ", 3, gefuehlsWahrnehmung));
        questionRepository.save(new Question("Es wäre gut, wenn ich meine Erfolge etwas mehr genießen könnte. ", 3, feedbackVerwertung));
        questionRepository.save(new Question("Ich möchte lernen, fremde Meinungen genauso ernst zu nehmen wie meine eigene. ", 3, parallelVerarbeitung));
        questionRepository.save(new Question("Auf meine unbewusste Intelligenz kann ich mich noch nicht genug verlassen. ", 3, zugangUnbewussten));
        questionRepository.save(new Question("Es wäre schön, wenn ich schneller auf Dinge aufmerksam würde, die für mich relevant sind. ", 3, wachsamkeit));
        questionRepository.save(new Question("Ich möchte gern lernen, meine Gefühle besser regulieren zu können. ", 3, affektRegulation));

        List<Question> questions = questionRepository.findAll();
        int[] answers1 = {3, 3, 3, 3, 4, 3, 2, 3, 2, 2, 2, 2, 2, 1, 3, 3, 4, 3, 2, 3, 4, 3, 3, 2, 2, 2, 3, 3, 3, 2, 4, 3, 4, 3, 3, 3,
            3, 2, 3, 2, 4, 1, 4, 2, 4, 4, 2, 1,
            1, 2, 4, 2, 3, 3, 1, 3, 4, 4, 2, 2, 2, 2, 3, 4, 3, 4, 3, 2, 1};
        int[] answers2 = {2, 3, 4, 2, 3, 4, 1, 2, 4, 1, 2, 3, 4, 4, 1, 2, 3, 4, 4, 1, 2, 3, 4, 2, 3, 1, 2, 2, 2, 1, 1, 3, 2, 1, 3, 3,
            3, 1, 4, 3, 2, 2, 1, 4, 4, 3, 2, 3,
            2, 2, 3, 4, 1, 3, 1, 3, 2, 4, 3, 4, 1, 2, 2, 1, 4, 1, 2, 1, 1};
        int[] answers3 = {1, 4, 3, 2, 2, 1, 3, 4, 4, 2, 1, 2, 3, 2, 4, 3, 4, 1, 2, 2, 1, 4, 4, 4, 3, 2, 1, 3, 2, 2, 4, 2, 3, 2, 1, 1,
            4, 4, 4, 1, 3, 2, 1, 3, 2, 3, 4, 3,
            1, 1, 2, 3, 4, 2, 3, 4, 2, 1, 3, 1, 4, 1, 2, 3, 4, 2, 4, 3, 1};
        int counter = 0;
        for (Question q : questions) {
            int answer1 = answers1[counter];
            int answer2 = answers2[counter];
            int answer3 = answers3[counter++];
            answerRepository.save(new Answer(answer1, q, applicationUser1));
            answerRepository.save(new Answer(answer2, q, applicationUser2));
            answerRepository.save(new Answer(answer3, q, applicationUser3));

        }

    }

    @Test
    public void calculatingAssessment1Process() {
        List<ProcessTraitDto> traitsProcess = singleAssessment.calculateProcessTraits(applicationUser1);
        assertAll(
            () -> {
                assertNotNull(traitsProcess);
                assertEquals(12, traitsProcess.get(0).getResult());
                assertEquals(13, traitsProcess.get(1).getResult());
                assertEquals(20, traitsProcess.get(2).getResult());
                assertEquals(16, traitsProcess.get(3).getResult());
                assertEquals(20, traitsProcess.get(4).getResult());
                assertEquals(12, traitsProcess.get(5).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment1Neuro() {
        List<NeuroTraitDto> traitsNeuro = singleAssessment.calculateNeuroTraits(applicationUser1);
        assertAll(
            () -> {
                assertNotNull(traitsNeuro);
                assertEquals(8, traitsNeuro.get(0).getResult());
                assertEquals(4, traitsNeuro.get(1).getResult());
                assertEquals(2, traitsNeuro.get(2).getResult());
                assertEquals(6, traitsNeuro.get(3).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment1Psycho() {
        List<PsychoTraitDto> traitsPsycho = singleAssessment.calculatePsychoTraits(applicationUser1);
        assertAll(
            () -> {
                assertNotNull(traitsPsycho);
                assertEquals(6, traitsPsycho.get(0).getResult());
                assertEquals(8, traitsPsycho.get(1).getResult());
                assertEquals(6, traitsPsycho.get(2).getResult());
                assertEquals(4, traitsPsycho.get(3).getResult());
                assertEquals(5, traitsPsycho.get(4).getResult());
                assertEquals(5, traitsPsycho.get(5).getResult());
                assertEquals(6, traitsPsycho.get(6).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment2Process() {
        List<ProcessTraitDto> traitsProcess = singleAssessment.calculateProcessTraits(applicationUser2);
        assertAll(
            () -> {
                assertNotNull(traitsProcess);
                assertEquals(14, traitsProcess.get(0).getResult());
                assertEquals(12, traitsProcess.get(1).getResult());
                assertEquals(13, traitsProcess.get(2).getResult());
                assertEquals(6, traitsProcess.get(3).getResult());
                assertEquals(16, traitsProcess.get(4).getResult());
                assertEquals(17, traitsProcess.get(5).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment2Neuro() {
        List<NeuroTraitDto> traitsNeuro = singleAssessment.calculateNeuroTraits(applicationUser2);
        assertAll(
            () -> {
                assertNotNull(traitsNeuro);
                assertEquals(6, traitsNeuro.get(0).getResult());
                assertEquals(3, traitsNeuro.get(1).getResult());
                assertEquals(7, traitsNeuro.get(2).getResult());
                assertEquals(4, traitsNeuro.get(3).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment2Psycho() {
        List<PsychoTraitDto> traitsPsycho = singleAssessment.calculatePsychoTraits(applicationUser2);
        assertAll(
            () -> {
                assertNotNull(traitsPsycho);
                assertEquals(4, traitsPsycho.get(0).getResult());
                assertEquals(3, traitsPsycho.get(1).getResult());
                assertEquals(2, traitsPsycho.get(2).getResult());
                assertEquals(2, traitsPsycho.get(3).getResult());
                assertEquals(5, traitsPsycho.get(4).getResult());
                assertEquals(5, traitsPsycho.get(5).getResult());
                assertEquals(1, traitsPsycho.get(6).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment3Process() {
        List<ProcessTraitDto> traitsProcess = singleAssessment.calculateProcessTraits(applicationUser3);
        assertAll(
            () -> {
                assertNotNull(traitsProcess);
                assertEquals(15, traitsProcess.get(0).getResult());
                assertEquals(14, traitsProcess.get(1).getResult());
                assertEquals(16, traitsProcess.get(2).getResult());
                assertEquals(14, traitsProcess.get(3).getResult());
                assertEquals(12, traitsProcess.get(4).getResult());
                assertEquals(7, traitsProcess.get(5).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment3Neuro() {
        List<NeuroTraitDto> traitsNeuro = singleAssessment.calculateNeuroTraits(applicationUser3);
        assertAll(
            () -> {
                assertNotNull(traitsNeuro);
                assertEquals(6, traitsNeuro.get(0).getResult());
                assertEquals(6, traitsNeuro.get(1).getResult());
                assertEquals(4, traitsNeuro.get(2).getResult());
                assertEquals(6, traitsNeuro.get(3).getResult());
            }
        );
    }

    @Test
    public void calculatingAssessment3Psycho() {
        List<PsychoTraitDto> traitsPsycho = singleAssessment.calculatePsychoTraits(applicationUser3);
        assertAll(
            () -> {
                assertNotNull(traitsPsycho);
                assertEquals(4, traitsPsycho.get(0).getResult());
                assertEquals(6, traitsPsycho.get(1).getResult());
                assertEquals(4, traitsPsycho.get(2).getResult());
                assertEquals(6, traitsPsycho.get(3).getResult());
                assertEquals(4, traitsPsycho.get(4).getResult());
                assertEquals(7, traitsPsycho.get(5).getResult());
                assertEquals(9, traitsPsycho.get(6).getResult());
            }
        );
    }
}

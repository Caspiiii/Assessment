package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.NeuroTrait;
import at.ac.tuwien.sepr.groupphase.backend.entity.ProcessTrait;
import at.ac.tuwien.sepr.groupphase.backend.entity.PsychoTrait;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.entity.Trait;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TraitRepositroy;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

//@Profile({"generateData", "withAnswers"})
@Component
public class QuestionDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private QuestionRepository questionRepository;

    private TraitRepositroy traitRepositroy;

    @Autowired
    private DataSource dataSource;

    public QuestionDataGenerator(QuestionRepository questionRepository, TraitRepositroy traitRepositroy) {
        this.questionRepository = questionRepository;
        this.traitRepositroy = traitRepositroy;
    }


    @PostConstruct
    @Transactional
    public void generateQuestions() {

        LOGGER.info("generate Traits");

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

        deleteQuestions();

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


        LOGGER.info("generate Questions");

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


    }


    private void deleteQuestions() {
        if (!(questionRepository.findAll()).isEmpty()) {
            questionRepository.deleteAll();

        }
    }

}

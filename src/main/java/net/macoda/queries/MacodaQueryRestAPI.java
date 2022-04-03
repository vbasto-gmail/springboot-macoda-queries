package net.macoda.queries;

import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swrlapi.sqwrl.SQWRLQueryEngine;

@RestController
public class MacodaQueryRestAPI {

	OWLOntologyManager ontologyManager;
    OWLOntology ontology;
    SQWRLQueryEngine queryEngine;

    @GetMapping("/queryapi")
	public String run() throws IOException {
    	MacodaSWRLAPI macodaAPI = new MacodaSWRLAPI("MaCODA-labelized.owl");
        // Predefined queries presented in the macoda.club web site
        // 0. What are the metaheuristics published after 2015 ?
        // 1. What are the Java libraries implementing pNSGA-II ?  
        // 2. What are the metaheuristics that were tested in Knapsack problem ?
        // 3. What are the metaheuristics that were tested in ZDT problem having Java libraries implementations ?
        // 4. Which order relations have been proposed to many-objective optimisation ?
        // 4. A MODELAÇÂO AINDA NÃO ESTÀ COMPLETA, PENSO ATÈ QUE ESTÀ ERRADA, PARA RESPONDER A ESTA QUERY
        // 5. Who are the researchers working both in decomposition-based and indicator-based metaheuristics ?
        // 5. NA ONTOLOGIA NÃO ESTÃO PRESENTES DADOS QUE CORRESPONDAM A ESTA QUERY
    	ArrayList<String> aQuery = new ArrayList<String>();
    	aQuery.add("MetaHeuristic(?_metaheuristic_) ^ hasDevelopingYear(?_metaheuristic_ , ?_hasdevelopingyear_) ^ swrlb:greaterThanOrEqual(?_hasdevelopingyear_ , 2015) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_hasdevelopingyear_) ^ sqwrl:orderBy(?_hasdevelopingyear_)");
    	aQuery.add("MetaHeuristic(pNSGA-II) ^ useLibrary(pNSGA-II , ?_library_) ^ hasImplementationLanguage(pNSGA-II , Java) -> sqwrl:select(?_library_) ^ sqwrl:orderBy(?_library_)");
    	aQuery.add("MetaHeuristic(?_metaheuristic_) ^ canSolve(?_metaheuristic_ , ?_knapsack_) ^ Knapsack(?_knapsack_) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_knapsack_) ^ sqwrl:orderBy(?_metaheuristic_)");
    	aQuery.add("MetaHeuristic(?_metaheuristic_) ^ ZDT(?_zdt_) ^ canSolve(?_metaheuristic_ , ?_zdt_) ^ useLibrary(?_metaheuristic_ , ?_implementationlibrary_) ^ ImplementationLibrary(?_implementationlibrary_) ^ hasImplementationLanguage(?_metaheuristic_ , Java) ->  sqwrl:select(?_zdt_) ^ sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_implementationlibrary_)");
    	aQuery.add("MetaHeuristic(?_metaheuristic_) ^ isManyObjectiveProblem(?_metaheuristic_ , true) ^ usesOrderRelation(?_metaheuristic_ , ?_orderrelation_) ^ OrderRelation(?_orderrelation_) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_orderrelation_) ^ sqwrl:orderBy(?_orderrelation_)");
    	aQuery.add("Indicator_based(?_indicatorbased_) ^ Decomposition_based(?_decompositionbased_) ^ Researcher(?_researcher_) ^ hasAuthor(?_indicatorbased_ , ?_researcher_) ^ hasAuthor(?_decompositionbased_ , ?_researcher_) -> sqwrl:select(?_indicatorbased_) ^ sqwrl:select(?_researcher_)");    

    	//macodaAPI.executeSWRLQuery(sQuery, bAlsoWriteToConsole, bAlsoWriteToHtmlFile)
		return macodaAPI.executeSWRLQuery(aQuery.get(0), true, true);
	}
}
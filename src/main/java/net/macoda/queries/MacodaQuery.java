package net.macoda.queries;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.File;

@RestController
public class MacodaQuery {
	@GetMapping("/query")
	public String run() {  
//		
	    String output = "";
//		File owlFile = new File("ADS.owl");
//		File owlFile = new File("MaCODA.owl");
		File owlFile = new File("PMOEA.owl");	    
		try {
	      // Loading an OWL ontology using the OWLAPI
	      OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	      OWLOntology ontology =  ontologyManager.loadOntologyFromOntologyDocument(owlFile);
	      //OWLOntology ontology = ontologyManager.createOntology();	      
	      
	      // Create SQWRL query engine using the SWRLAPI
	      SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);

	      // Create and execute a SQWRL query using the SWRLAPI
	      // Os algoritmos que funcionam bem para problemas com 2,3,4,etc. objetivps são diferentes
	      String numberOfObjectives = "2";
	      String query = "Algorithm(?alg) ^ "   
	    	+ "minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ swrlb:lessThanOrEqual(?min,"+numberOfObjectives+")"
	    	+ "maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max) ^ swrlb:greaterThanOrEqual(?max,"+numberOfObjectives+")"
	    	+ " -> sqwrl:select(?alg) ^ sqwrl:orderBy(?alg)";  
	      
	      query = "PMOEA:OWLClass_78aa5e30_4a52_440d_b144_1b2414334f9e(?a) ^ PMOEA:OWLClass_dc777a2b_5b14_4674_ba36_f1f9e90da118(?p) ^ PMOEA:OWLObjectProperty_6341b9ed_8594_47b2_9bc7_3075bcde4d20(?a,?p) -> sqwrl:select(?a,?p)";
//	      query = "PMOEA:OWLClass_78aa5e30_4a52_440d_b144_1b2414334f9e(?a) -> sqwrl:select(?a)";
	      SQWRLResult result = queryEngine.runSQWRLQuery("q1", query);	      
	      //SQWRLResult result = queryEngine.runSQWRLQuery("q1", "swrlb:add(?x, 2, 2) -> sqwrl:select(?x)");	      
	      
	      // Process the SQWRL result
	      //output = "Query: \n" + query + "\n";
	      //output = output + "Result: ";
	      output = output + result.toString();
	      //if (result.next()) output = "x: " + result.getLiteral("x").getInteger();
	      while (false && result.next()) {
	    	  output = output + "{";
	    	  for (int i = 0; i < result.getNumberOfColumns(); i++) {
		    	  output = output + result.getColumnName(i) + ":" + "\"" + 
		    			  result. getNamedIndividual("alg").getShortName() + 
		    			  "\",";
	    	  }
	    	  output = output + "}";
	      }
	      
	    } catch (OWLOntologyCreationException e) {
	      System.err.println("Error creating OWL ontology: " + e.getMessage());
	      System.exit(-1);
	    } catch (SWRLParseException e) {
	      System.err.println("Error parsing SWRL rule or SQWRL query: " + e.getMessage());
	      System.exit(-1);
	    } catch (SQWRLException e) {
	      System.err.println("Error running SWRL rule or SQWRL query: " + e.getMessage());
	      System.exit(-1);
	    } catch (RuntimeException e) {
	      System.err.println("Error starting application: " + e.getMessage());
	      System.exit(-1);
	    }
	    
		return output;
	}
}

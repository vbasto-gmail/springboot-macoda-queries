package net.macoda.queries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

public class MacodaSWRLAPI {

	OWLOntologyManager ontologyManager;
    OWLOntology ontology;
    SQWRLQueryEngine queryEngine;
		
	public MacodaSWRLAPI(String ontologyOwlFile) throws IOException { // super();
		File owlFile = new File(ontologyOwlFile);
	    try {
	      // Loading an OWL ontology using the OWLAPI
	      ontologyManager = OWLManager.createOWLOntologyManager();
	      ontology =  ontologyManager.loadOntologyFromOntologyDocument(owlFile);

	      // Set ontology DefaultPrefix
	      ontologyManager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().setDefaultPrefix("http://www.semanticweb.org/macoda/ontologies/PMOEA#");

	      // Create SQWRL query engine using the SWRLAPI
	      queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
	      
	    } catch (OWLOntologyCreationException e) {
	      System.err.println("Error creating OWL ontology: " + e.getMessage());
	      System.exit(-1);
	    } catch (RuntimeException e) {
	      System.err.println("Error starting application: " + e.getMessage());
	      System.exit(-1);
	    }		
	    
	  }
	

	public String executeSWRLQuery(String sQuery, Boolean bAlsoWriteToConsole, Boolean bAlsoWriteToHtmlFile) throws IOException {
		 // Create and execute a SQWRL query using the SWRLAPI

	      // ***** NA GERAÇÃO DA QUERY DEVE APARECER RELAÇÃO_OBJECT (_canSolve_knapsack_)
//	    query = "MetaHeuristic(?_metaheuristic_) ^ canSolve(?_metaheuristic_ , ?_knapsack_) ^ Knapsack(?_knapsack_) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_knapsack_) ^ sqwrl:orderBy(?_metaheuristic_)";
	      // ***** NA GERAÇÃO DA QUERY DEVE APARECER RELAÇÃO_OBJECT (_canSolve_knapsack_)
	      
//	    query = "MetaHeuristic(?_metaheuristic_) ^ canSolve(?_metaheuristic_ , ?_knapsack_) ^ Knapsack(?_knapsack_) ^ useLibrary(?_metaheuristic_ , ?_implementationlibrary_) ^ ImplementationLibrary(?_implementationlibrary_) ^ hasImplementationLanguage(?_implementationlibrary_ , Java) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_implementationlibrary_) ^ sqwrl:orderBy(?_metaheuristic_) ^ sqwrl:orderBy(?_implementationlibrary_)";
//	    query = "MetaHeuristic(?_metaheuristic_) ^ isManyObjectiveOptimization(?_metaheuristic_ , true) ^ usesOrderRelation(?_metaheuristic_ , ?_orderrelation_) ^ OrderRelation(?_orderrelation_) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_orderrelation_) ^ sqwrl:orderBy(?_orderrelation_)";
//	    query = "MetaHeuristic(?_metaheuristic_) ^ isManyObjectiveProblem(?_metaheuristic_ , true) ^ usesOrderRelation(?_metaheuristic_ , ?_orderrelation_) ^ OrderRelation(?_orderrelation_) -> sqwrl:select(?_metaheuristic_) ^ sqwrl:select(?_orderrelation_) ^ sqwrl:orderBy(?_orderrelation_)";
//	    query = "Decomposition_based(?_decomposition_based_) ^ hasAuthor(?_decomposition_based_ , ?_researcher_) ^ Researcher(?_researcher_) ^ Indicator_based(?_indicator_based_) ^ hasAuthor(?_indicator_based_ , ?_researcher_) -> sqwrl:select(?_researcher_) ^ sqwrl:orderBy(?_researcher_)";
		String tabulatorColumnsTitlesAndConfigurations ="";
		String output = htmlHeader();
		String tabulatorData = "[";
		try {
			SQWRLResult result = queryEngine.runSQWRLQuery("q1", sQuery);
			// while (result.next()) {
			for (int iRows=0; iRows < result.getNumberOfRows(); iRows++) {
			  result.next();
			  tabulatorData += "{";
	    	  for (int iColumns = 0; iColumns < result.getNumberOfColumns(); iColumns++) {
	    		  tabulatorData = tabulatorData + result.getColumnName(iColumns) + ":\"" + 
	    				  result.getValue(iColumns).toString().replace("\"","") + "\","; // Os literais são devolvidos pela SWRL API entre aspas, o que perturba a consrução do html. Por isso, os caracteres " que vêm da SWRL API são substituidos por vazio
	    		  if (iRows==0) {
	    			  tabulatorColumnsTitlesAndConfigurations += "{title:\""
	    			  + result.getColumnName(iColumns).substring(1, result.getColumnName(iColumns).length() - 1) // retira o caracter "_" do inicio e fim da string, que são colocados nas variaveis da query SWRL quando é gerada a query
	    			  + "\", field:\"" + 
	    			  result.getColumnName(iColumns) + "\", headerFilter:\"input\"},\n";
//	    		  + "			{title:\"Building\", field:\"building\", headerFilter:\"input\", headerMenu:headerMenu},\n"
	    		  }
	    	  }
	    	  tabulatorData += "},";
			}
		  tabulatorData += "];";

		  if (bAlsoWriteToConsole) {
			  System.out.println("Query: " + sQuery);			  
			  System.out.println("Tabulator Table Header:");
			  System.out.println(tabulatorColumnsTitlesAndConfigurations);
			  System.out.println("Tabulator Table Data:");
			  System.out.println(tabulatorData);
		  }
		  // Limpar o prefixo (namespace) das entidades no resultado da query (para melhorar o aspeto do output)
		  tabulatorData = tabulatorData.replaceAll("PMOEA:", "");	  
		  // Limpar os tipos de dados que aparecem associados aos tipos de dados nativos no resultado das queries (v.g. "2015^^xsd:integer")
		  tabulatorData = tabulatorData.replaceAll("\\^\\^xsd:integer", "");

		  if (bAlsoWriteToConsole) {
			  System.out.println("\nTabulator Table Data Post-Processed (workspace prefix, etc.):");
			  System.out.println(tabulatorData);
		  }
		  
		  output += tabulatorData;
		  output += htmlEnd(tabulatorColumnsTitlesAndConfigurations);
		  
		  if (bAlsoWriteToHtmlFile) {
			  BufferedWriter writer = new BufferedWriter(new FileWriter("SWRL-Query-Result.html"));
			  writer.write(output);	    
			  writer.close();	  
		  }
		  
    	} catch (SWRLParseException e) {
	      output = "Error parsing SWRL rule or SQWRL query: " + e.getMessage();
	      if (bAlsoWriteToConsole) {System.err.println(output);}
	    } catch (SQWRLException e) {
	      output = "Error running SWRL rule or SQWRL query: " + e.getMessage();
	      if (bAlsoWriteToConsole) {System.err.println(output);}
	    }
		
		return output;

	}
	public String htmlHeader() {
		    return "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
		    		+ "<head>\n"
		    		+ "    <meta charset=\"utf-8\" />\n"
		    		+ "    <!--  <link href=\"https://unpkg.com/tabulator-tables@4.8.4/dist/css/tabulator.min.css\" rel=\"stylesheet\">  -->\n"
		    		+ "      <link href=\"https://vbasto-gmail.github.io/mycoda/tabulator.min.css\" rel=\"stylesheet\">\n"
		    		+ "  <!--  <script type=\"text/javascript\" src=\"https://unpkg.com/tabulator-tables@4.8.4/dist/js/tabulator.min.js\"></script>  -->\n"
		    		+ "    <script type=\"text/javascript\" src=\"https://vbasto-gmail.github.io/mycoda/tabulator.min.js\"></script>\n"
		    		+ "<!-- <script src=\"https://code.jquery.com/jquery-3.2.1.min.js\"></script> -->\n"
		    		+ "</head>\n"
		    		+ "<body>\n"
		    		+ "	<i><b>SQWRL Query Result</b></i>\n"
		    		+ "	<div id=\"example-table\"></div>\n"
		    		+ "    <script type=\"text/javascript\">\n"
		    		+ "\n"
		    		+ "	var tabledata = ";
	  }
	  
	  public String htmlEnd(String tabulatorColumnsTitlesAndConfigurations) {
		  return "//define row context menu contents\n"
			        		+ "var rowMenu = [\n"
			        		+ "    //{\n"
			        		+ "    //   label:\"<i class='fas fa-user'></i> Change Name\",\n"
			        		+ "    //   action:function(e, row){\n"
			        		+ "    //       row.update({name:\"Steve Bobberson\"});\n"
			        		+ "    //   }\n"
			        		+ "    //},\n"
			        		+ "    {\n"
			        		+ "        label:\"<i class='fas fa-check-square'></i> Select Row\",\n"
			        		+ "        action:function(e, row){\n"
			        		+ "            row.select();\n"
			        		+ "        }\n"
			        		+ "    },\n"
			        		+ "    {\n"
			        		+ "        separator:true,\n"
			        		+ "    },\n"
			        		+ "    {\n"
			        		+ "        label:\"Admin Functions\",\n"
			        		+ "        menu:[\n"
			        		+ "            {\n"
			        		+ "                label:\"<i class='fas fa-trash'></i> Delete Row\",\n"
			        		+ "                action:function(e, row){\n"
			        		+ "                    row.delete();\n"
			        		+ "                }\n"
			        		+ "            },\n"
			        		+ "        //    {\n"
			        		+ "        //        label:\"<i class='fas fa-ban'></i> Disabled Option\",\n"
			        		+ "        //        disabled:true,\n"
			        		+ "        //    },\n"
			        		+ "        ]\n"
			        		+ "    }\n"
			        		+ "]\n"
			        		+ "\n"
			        		+ "//define column header menu as column visibility toggle\n"
			        		+ "var headerMenu = function(){\n"
			        		+ "    var menu = [];\n"
			        		+ "    var columns = this.getColumns();\n"
			        		+ "\n"
			        		+ "    for(let column of columns){\n"
			        		+ "\n"
			        		+ "        //create checkbox element using font awesome icons\n"
			        		+ "        let icon = document.createElement(\"i\");\n"
			        		+ "        icon.classList.add(\"fas\");\n"
			        		+ "        icon.classList.add(column.isVisible() ? \"fa-check-square\" : \"fa-square\");\n"
			        		+ "\n"
			        		+ "        //build label\n"
			        		+ "        let label = document.createElement(\"span\");\n"
			        		+ "        let title = document.createElement(\"span\");\n"
			        		+ "\n"
			        		+ "        title.textContent = \" \" + column.getDefinition().title;\n"
			        		+ "\n"
			        		+ "        label.appendChild(icon);\n"
			        		+ "        label.appendChild(title);\n"
			        		+ "\n"
			        		+ "        //create menu item\n"
			        		+ "        menu.push({\n"
			        		+ "            label:label,\n"
			        		+ "            action:function(e){\n"
			        		+ "                //prevent menu closing\n"
			        		+ "                e.stopPropagation();\n"
			        		+ "\n"
			        		+ "                //toggle current column visibility\n"
			        		+ "                column.toggle();\n"
			        		+ "\n"
			        		+ "                //change menu item icon\n"
			        		+ "                if(column.isVisible()){\n"
			        		+ "                    icon.classList.remove(\"fa-square\");\n"
			        		+ "                    icon.classList.add(\"fa-check-square\");\n"
			        		+ "                }else{\n"
			        		+ "                    icon.classList.remove(\"fa-check-square\");\n"
			        		+ "                    icon.classList.add(\"fa-square\");\n"
			        		+ "                }\n"
			        		+ "            }\n"
			        		+ "        });\n"
			        		+ "    }\n"
			        		+ "\n"
			        		+ "   return menu;\n"
			        		+ "};	"
			        		+ "\n"
			        	+ "\n\n	var table = new Tabulator(\"#example-table\", {\n"
			        	+ "		data:tabledata,\n"
			        	+ "		layout:\"fitDatafill\",\n"
			        	+ "		rowContextMenu: rowMenu, //add context menu to rows \n"
			        	+ "		pagination:\"local\",\n"
			        	+ "		paginationSize:5,\n"
			       		+ "		paginationSizeSelector:[5, 10, 20, 40, 80],\n"  
			        	+ "		movableColumns:true,\n"        	
			    		+ "		paginationCounter:\"rows\",\n"
			       		+ "		initialSort:[{column:\"building\",dir:\"asc\"},],"	
			       		+ "		columns:[\n"
			       		+ tabulatorColumnsTitlesAndConfigurations
			       		/*		       		
			       		+ "			{title:\"Buiding\", field:\"building\", headerFilter:\"input\", headerMenu:headerMenu},\n"
			       		+ "			{title:\"Room\", field:\"room\", headerFilter:\"input\", headerMenu:headerMenu},\n"
			       		+ "			{title:\"Normal Capacity\", field:\"normalcapacity\", hozAlign:\"center\", headerMenu:headerMenu},\n"
			       		+ "			{title:\"Exams Capacity\", field:\"examscapacity\", hozAlign:\"center\", headerMenu:headerMenu},\n"
			        	+ "			{title:\"Amount of RP\", field:\"amountofroomproperties\", hozAlign:\"center\", headerMenu:headerMenu},	\n"
			        	+ "			{title:\"Indexes of RP\", field:\"indexesofroomproperties\", headerFilter:\"input\", hozAlign:\"center\", headerMenu:headerMenu},\n"
			       		+ "			{title:\"Room Properties (RP)\", field:\"roomproperties\", hozAlign:\"center\", headerFilter:\"input\", headerMenu:headerMenu},\n"
	*/
			       		+ "		],\n"
			       		+ "	});\n"
			       		+ "	</script>\n"
			        	+ "</body>\n"
			        	+ "</html>";
	  	}
}

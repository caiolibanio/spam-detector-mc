package detector;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Classificador {
	
	private static Instances emails;
	private static double hamCount;
	private static double spamCount;
	
	
    public static void main(String[] args) throws Exception {

    	boolean verbose = false;
    	String out = "";
    	Instances D = null;
    	hamCount = 0;
    	spamCount = 0;
    	
    	//Definindo os valores da entrada
    	
        if(args.length <3 ){
        	System.out.println("Entrada invalida !!!");
        }
    	else if(args[0].isEmpty()|| args[1].isEmpty()|| args[2].isEmpty()){
    		System.out.println("Entrada inválida !!!");
    	}
    	else{
    		if(args[0].equalsIgnoreCase("-v")){
    			verbose = true;
    		}
    		
    		String treinoPath;
    		
    		try{
    			DataSource source;
    			if(verbose){
    				source = new DataSource(args[2]);
    				treinoPath = args[2];
    				D = source.getDataSet();
    				
    			}
    			else{
    				System.out.println(args[1]);
    				treinoPath = args[1];
    				
    				
    	    		if(treinoPath.contains(".arff")){
    	    			source = new DataSource(args[1]);
    	    			D = source.getDataSet();
    	    		}else{
    	    			System.out.println("chamando metodo");
    	    			D = geraTreino(treinoPath);
    	    			System.out.println("terminou");
    	    			System.out.println(D == null);
    	    		}

    				
    			}
               
            
            //espeficicao do atributo classe
    		
    			
//    		Instances D;
//    		if(treinoPath.contains(".arff")){
//    			D = source.getDataSet();
//    		}else{
//    			System.out.println("chamando metodo");
//    			D = geraTreino(treinoPath);
//    		}
    			
    		
    		
    		  
    		
            if (D.classIndex() == -1) {
                D.setClassIndex(D.numAttributes() - 1);
            }
            
           //Construcao do modelo classificador (treinamento)
          
            Classifier classificador = null;
            
         
            String[] palavras = new String[]{"make","address","all","3d","our","over","remove","internet",
        			"order","mail","receive","will","people","report","addresses","free","business","email",
        			"you","credit","your","font","000","money","hp","hpl","george","650","lab","labs","telnet",
        			"857","data","415","85","technology","1999", "parts","pm","direct","cs","meeting","original",
        			"project","re","edu","table","conference",";","(","[","!","$","#", "capital_run_length_average",
        			"capital_run_length_longest", "capital_run_length_total"};
            
            File arquivos[];  
//            File diretorio = new File("C:/Users/User/Desktop/Projetos Java/SpamDetector/enron2/ham"); 
            File diretorio;
            if(verbose){
            	 diretorio = new File(args[3]);  
            }
            else{
            	 diretorio = new File(args[2]);  
            }
            arquivos = diretorio.listFiles();
            
            //Laco para imprimir os valores das analizes de cada email de acordo
            //com o algoritmo passado na entrada
            
            FastVector fvWekaAttributes = new FastVector(58);
            for (String i : palavras ){
            	Attribute Attribute1;
            	if(i.equals("capital_run_length_average")){
            		Attribute1 = new Attribute(i);
            	}
            	else if(i.equals("capital_run_length_longest")){
            		Attribute1 = new Attribute(i);
            	}
            	else if(i.equals("capital_run_length_total")){
            		Attribute1 = new Attribute(i);
            	}
            	else{
            		Attribute1 = new Attribute("word_freq_"+i);
            	}
            	fvWekaAttributes.addElement(Attribute1);
            }
            
            FastVector fvClassVal = new FastVector(2);
            fvClassVal.addElement("0");
            fvClassVal.addElement("1");
            Attribute ClassAttribute = new Attribute("spambase", fvClassVal);
            fvWekaAttributes.addElement(ClassAttribute);
            Instances emails = new Instances("emails", fvWekaAttributes, 500);
            emails.setClassIndex(57);
            
            
            for(int i=0;i<arquivos.length;i++){
           	 try {  
                    FileReader reader = new FileReader(arquivos[i]);  
                    BufferedReader input = new BufferedReader(reader);  
                    String linha;  
                    String email = "";
                    while ((linha = input.readLine()) != null) {  
                      email +=linha+"\n";
                    }  
                    input.close();  
                    HashMap<String, Double> mapa = attributesScan(email);
                    Instance objeto = new Instance(58);
                    Instance objeto2 = new Instance(58);
                    objeto.setDataset(D);
                    objeto2.setDataset(D);
                    int cont = 0;
					for (String z : palavras) {
						objeto.setValue(cont, mapa.get(z));
						objeto2.setValue((Attribute) fvWekaAttributes.elementAt(cont) , mapa.get(z));
						cont++;
					}
		            if(args[0].equalsIgnoreCase("ibk") || args[1].equalsIgnoreCase("ibk")){
		            	classificador = new IBk(3);
                    	classificador.buildClassifier(D);
		            }else if(args[0].equalsIgnoreCase("bayes") || args[1].equalsIgnoreCase("bayes")){
		            	classificador = new NaiveBayes();
                    	classificador.buildClassifier(D);
		            } else if(args[0].equalsIgnoreCase("ib1") || args[1].equalsIgnoreCase("ib1")){
		            	classificador = new IB1();
                    	classificador.buildClassifier(D);
		            }
		            if(classificador.classifyInstance(objeto)==1){
		            		objeto2.setValue((Attribute) fvWekaAttributes.elementAt(57), "1");
		            }
		            else{
		            	objeto2.setValue((Attribute) fvWekaAttributes.elementAt(57), "0");
		            }
                    emails.add(objeto2);
                    // O usuario escolheu o algoritmo IBK
                    if(args[0].equalsIgnoreCase("ibk") || args[1].equalsIgnoreCase("ibk")){
               
                    	classificador = new IBk(3);
                    	classificador.buildClassifier(D);
                    	if(verbose){
                    		if(classificador.classifyInstance(objeto)==1){
                             	 out = out + arquivos[i].getName() + ": " + "spam" + "\n";
                             	 spamCount++;
                              }
                              else{
                             	 out = out + arquivos[i].getName() + ": " + "ham" + "\n";
                             	 hamCount++;
                              }
                    		out = out + "---------------------------------------------------------" + "\n";
                    	}else{
//                    		out = out + getStatistics(D, classificador,emails);
//                            break;
                    	}

                    }
                    
                 // O usuario escolheu o algoritmo Kstar
                    else if(args[0].equalsIgnoreCase("bayes") || args[1].equalsIgnoreCase("bayes")){
                    	classificador = new NaiveBayes();
                    	classificador.buildClassifier(D);
                    	if(verbose){
                    	if(classificador.classifyInstance(objeto)==1){
                    		 out = out + arquivos[i].getName() + ": " + "spam" + "\n";
                    		 spamCount++;
                           }
                           else{
                        	   out = out + arquivos[i].getName() + ": " + "ham" + "\n";
                        	   hamCount++;
                           }
                    	out = out + "---------------------------------------------------------" + "\n";}
                    	else{
//                    		out = out + getStatistics(D, classificador,emails);
//                            break;
                    	}
                    	
                    }
                    
                 // O usuario escolheu o algoritmo IB1
                    else if(args[0].equalsIgnoreCase("ib1") || args[1].equalsIgnoreCase("ib1")){
                    	classificador = new IB1();
                    	classificador.buildClassifier(D);
                    	if(verbose){
                    	if(classificador.classifyInstance(objeto)==1){
                    		out = out + arquivos[i].getName() + ": " + "spam" + "\n";
                    		spamCount++;
                           }
                           else{
                        	   out = out + arquivos[i].getName() + ": " + "ham"+ "\n";
                        	   hamCount++;
                           }
                           out = out + "---------------------------------------------------------" + "\n";}
                    	else{
//                    		out = out + getStatistics(D, classificador,emails);
//                            break;
                    	}
                    	
                    }else{
                    	System.out.println("Digite um classificador valido !!!");
                    }
                  } catch (IOException ioe) {  
                     System.out.println("Não foi possível ler o arquivo: " + arquivos[1].getName());  
                  }  
            	}
            	
            	if(verbose && classificador != null){
            		out = out + getStatistics(D, classificador,emails);
            	}
            	
            	if(! verbose){
            		out = out + getStatistics(D, classificador,emails);
            	}
            	
            	
            	saveOutFile(out);
            	System.out.println("Executado com Sucesso !!! Abra o arquivo out.txt no diretorio atual para visualizar os resultados." );
            
    		}
    		catch (Exception e) {
    			e.printStackTrace();
				System.out.println("Verifique o diretorio !!!");
			}
    	}
    	
         
    }
    
    private static Instances geraTreino(String treinoPath) {
		//TODO
    	File arquivosHam[];
    	File arquivosSpam[]; 
    	Instances D = null;
    	File diretorioHam = new File(treinoPath + "\\ham");
    	File diretorioSpam = new File(treinoPath + "\\spam");
    	arquivosHam = diretorioHam.listFiles();
    	arquivosSpam = diretorioSpam.listFiles();
    	
    	String arffString = buildArffString();
    	
    	arffString = trataBase(arquivosHam, arffString, true);
    	arffString = trataBase(arquivosSpam, arffString, false);
    	
    	
    	try {
    		BufferedWriter out = new BufferedWriter(new FileWriter("arffDinamico.arff"));
    		out.write(arffString);
    		out.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    	
    	try {
			DataSource source = new DataSource("C:/Users/Caio/workspace/SpamDetector/arffDinamico.arff");
			System.out.println("source:" + source == null);
			D = source.getDataSet();
			System.out.println("5");
			System.out.println("source:" + source == null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return D;
	}

	private static String trataBase(File[] arquivos, String arffString, boolean ham) {
		
		for (int i = 0; i < arquivos.length; i++) {

			try {

				FileReader reader = new FileReader(arquivos[i]);
				BufferedReader input = new BufferedReader(reader);
				String linha;
				String email = "";
				while ((linha = input.readLine()) != null) {
					email += linha + "\n";
				}
				input.close();
				HashMap<String, Double> mapa = attributesScan(email);
				System.out.println("2");
				Object[] listaValores = mapa.values().toArray();
				System.out.println("3");

				for (int j = 0; j < listaValores.length; j++) {
					arffString += (double)listaValores[j] + ",";

				}
				System.out.println("4");
				if(ham){
					arffString += "0\n";
				}else{
					arffString += "1\n";
				}

			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}

		}
		

		
		return arffString;
	}

	private static String buildArffString() {
		
		String[] palavras = new String[]{"make","address","all","3d","our","over","remove","internet",
    			"order","mail","receive","will","people","report","addresses","free","business","email",
    			"you","credit","your","font","000","money","hp","hpl","george","650","lab","labs","telnet",
    			"857","data","415","85","technology","1999", "parts","pm","direct","cs","meeting","original",
    			"project","re","edu","table","conference",";","(","[","!","$","#", "capital_run_length_average",
    			"capital_run_length_longest", "capital_run_length_total"};
		
		String data = "@relation spambase\n\n";
		
		for(int i = 0; i < palavras.length; i++){
			
			if(! palavras[i].contains("capital") && palavras[i].length() > 1){
				data += "@attribute word_freq_" + palavras[i] + "     numeric\n";
			}else if(palavras[i].length() == 1){
				data += "@attribute char_freq_" + palavras[i] + "     numeric\n";	
			}else{
				data += "@attribute " + palavras[i] + "     numeric\n";
			}
			
		}
		
		
		data += "@attribute spam     {0,1}\n\n@data\n\n";
		
		
		
		return data;
	}

	private static void saveOutFile(String outText){
		
    	String counts = "\n\nSpam: " + ((spamCount / ( hamCount + spamCount )) * 100) + "%"+ "\nHam: " + ((hamCount / ( hamCount + spamCount )) * 100) + "%" + "\nTotal: " + (spamCount + hamCount);
    	outText += counts + "\n\n" + new Date(System.currentTimeMillis());
    	
    	
    	try {
    		BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
    		out.write(outText);
    		out.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    	
    }
    
    private static String getStatistics(Instances D, Classifier C, Instances T) throws Exception{
    	Evaluation evaluation = new Evaluation(D);
    	evaluation.evaluateModel(C,T);
        return "precision: " + evaluation.precision(0) + "\n" + "recall: " + evaluation.recall(0) + "\n" + "f-measure: " + evaluation.fMeasure(0);
    	
    }
    
    /**
     * Metodo que retorna a PORCENTAGEM de ocorrencias dos caracteres listados, em forma de um HashMap
     * @param mensagem Mensagem a ser Analizada
     * @return HashMap com as chaves ( palavras ) e valores ( porcentagem da ocorrencia da palavra )
     */
    private static HashMap<String, Double> attributesScan(String mensagem){
    	HashMap<String, Double> mapaOcorrencias = new HashMap<String, Double>();
    	
    	String[] palavras = new String[]{"make","address","all","3d","our","over","remove","internet",
    			"order","mail","receive","will","people","report","addresses","free","business","email",
    			"you","credit","your","font","000","money","hp","hpl","george","650","lab","labs","telnet",
    			"857","data","415","85","technology","1999", "parts","pm","direct","cs","meeting","original",
    			"project","re","edu","table","conference",";","(","[","!","$","#", "capital_run_length_average",
    			"capital_run_length_longest", "capital_run_length_total"};
    	
    	for(int i = 0; i < palavras.length; i++){
    		mapaOcorrencias.put(palavras[i], 0.0);
    	}
    	
    	String[] aux = mensagem.split(" ");
    	Object[] chaves = mapaOcorrencias.keySet().toArray();
    	
    	
    	for(int i = 0; i < chaves.length; i++){
    		
    		for(int j = 0; j < aux.length; j++){
    			if(aux[j].contains(chaves[i].toString())){
    				mapaOcorrencias.put(chaves[i].toString(), mapaOcorrencias.get(chaves[i].toString()) + 1.0);
    			}
    		}
    	}
    	
    	
    	for(int i = 0; i < chaves.length; i++){
    		mapaOcorrencias.put(chaves[i].toString(), (mapaOcorrencias.get(chaves[i].toString()) / aux.length) * 100.0 );
    	}
    	
    	
    	mapaOcorrencias.put("capital_run_length_average", capital_run_length_average(mensagem));
    	mapaOcorrencias.put("capital_run_length_longest", capital_run_length_longest(mensagem));
    	mapaOcorrencias.put("capital_run_length_total", capital_run_length_total(mensagem));
    	
    	
		return mapaOcorrencias;
    	
    }
    /**
     * Metodo que retorna a media de letras maiusculas da mensagem
     * @param mensagem Mensagem a ser Analizada
     * @return Numero medio de letras maiusculas na mensagem
     */
    private static double capital_run_length_average(String mensagem){
    	String[] aux = mensagem.split(" ");
    	double contLetrasMaiusculas = 0;
    	double contPalavrasMaiusculas = 0;
    	for(int i = 0; i < aux.length; i++){
    		String auxString = aux[i].trim();
    		if(auxString.toUpperCase().equals(auxString)){
    			contLetrasMaiusculas = contLetrasMaiusculas + auxString.length();
    			contPalavrasMaiusculas++;
    		}
    	}
    	
    	double media = contLetrasMaiusculas / contPalavrasMaiusculas;
    	return media;
    	
    }
    /**
     * Metodo que retorna o tamanho da maior palavra maiuscula na mensagem
     * @param mensagem Mensagem a ser Analizada
     * @return Tamanho da maior palavra totalmente capitalizada
     */
    private static double capital_run_length_longest(String mensagem){
    	String[] aux = mensagem.split(" ");
    	double maiorPalavraMaiuscula = 0.0;
    	
    	for(int i = 0; i < aux.length; i++){
    		String auxString = aux[i].trim();
    		if(auxString.toUpperCase().equals(auxString)){
    			if(auxString.length() > maiorPalavraMaiuscula){
    				maiorPalavraMaiuscula = auxString.length();
    			}
    			
    		}
    	}
    	
    	return maiorPalavraMaiuscula;
    	
    }
    /**
     * Metodo que retorna o total de letras maiusculas de palavras totalmente capitalizadas
     * @param mensagem Mensagem a ser Analizada
     * @return Tamanho total das palavras totalmente capitalizadas
     */
    private static double capital_run_length_total(String mensagem){
    	String[] aux = mensagem.split(" ");
    	double contLetrasMaiusculas = 0;
    	for(int i = 0; i < aux.length; i++){
    		String auxString = aux[i].trim();
    		if(auxString.toUpperCase().equals(auxString)){
    			contLetrasMaiusculas = contLetrasMaiusculas + auxString.length();
    		}
    	}
    	return contLetrasMaiusculas;
    }
    
}
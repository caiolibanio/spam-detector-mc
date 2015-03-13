package detector;



import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.lazy.IBk;
public class TesteIBk {
//    public static void main(String[] args) throws Exception {
//        //------------------------------------------------------
//        // (1) importação da base de dados de treinamento
//        //------------------------------------------------------
//         DataSource source = new DataSource("weather.arff");
//         Instances D = source.getDataSet();
//         
//         // 1.1 - espeficicação do atributo classe
//         if (D.classIndex() == -1) {
//             D.setClassIndex(D.numAttributes() - 1);
//         }
//        //------------------------------------------------------
//        // (2) Construção do modelo classificador (treinamento)
//        //------------------------------------------------------
//         IBk k3 = new IBk(3);
//         k3.buildClassifier(D);
//         
//         
//        //------------------------------------------------------
//        // (3) Classificando um novo exemplo
//        //------------------------------------------------------
//         
//         // 3.1 criação de uma nova instância
//         Instance newInst = new Instance(5);
//         newInst.setDataset(D);
//         newInst.setValue(0, "rainy");
//         newInst.setValue(1, 71);
//         newInst.setValue(2, 79);
//         newInst.setValue(3, "TRUE");
//         // 3.2 classificação de uma nova instância
//         double pred = k3.classifyInstance(newInst);
//         
// 
//         // 3.3 imprime o valor de pred
//         System.out.println("Predição: " + pred);
//         
//    }
}

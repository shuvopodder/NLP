/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irevaluate;

/**
 *
 * @author Shuvo Podder
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Shuvo Podder
 */
public class IREvaluate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Map<String, List<Integer>> dictionary = new HashMap<String, List<Integer>>();
        Map<PackingListRow, Integer> items = new HashMap<PackingListRow, Integer>();
        HashMap<Integer, Double> tw = new HashMap<>();//term weight
        HashMap<Integer, Double> tw2 = new HashMap<>();//term weight system 2
        HashMap<Integer, Integer> rv = new HashMap<>();//term relivence
        HashMap<Integer, Integer> rv2 = new HashMap<>();//term relivence system2
        File dir = new File("DocFile");
        String[] fileNames = dir.list();
        int i = 1, j = 0;
        int total[] = new int[100];
        for (String fileName : fileNames) {
            File f = new File(dir, fileName);

            String st = "";
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((st = br.readLine()) != null) {
                st = normalizeText(st);

                Pattern p = Pattern.compile("[a-zA-Z]+");
                Matcher m = p.matcher(st.toLowerCase());

                while (m.find()) {
                    String word = m.group(); //spilt word from sentense

                    if (dictionary.get(word) == null) {
                        dictionary.put(word, new ArrayList<Integer>());
                    }
                    dictionary.get(word).add(i);
                    addItem(items, new PackingListRow(word, i), 1);
                }
                j++;
            }
            total[i] = j;
            i++;
        }
        System.out.println(i);
        removeDuplicate(dictionary);
        dictionary = Sort(dictionary);//sort terms in dictionary
        System.out.println(dictionary);
        for (Map.Entry<String, List<Integer>> s : dictionary.entrySet()) {
            //System.out.println("Frequency:"+s.getKey()+"="+ dictionary.get(s.getKey()).size());
        }

        String st = "love adoreable heart ";

        List<String> ss = new ArrayList<>(Arrays.asList(st.toLowerCase().split(" ")));
        ListIterator<String> iterator = ss.listIterator();

        //system2
        Map<String, Double> m = new HashMap<>();//query frequency
        int freq = ss.size();
        for (int q = 0; q < freq; q++) {
            if (!m.containsKey(ss.get(q))) {
                m.put(ss.get(q), new Double(1.0));
            } else {
                m.put(ss.get(q), m.get(ss.get(q)) + 1.0);
            }
        }
        System.out.println(m);
        for (int q = 0; q < m.size(); q++) {

            m.put(ss.get(q), new Double(m.get(ss.get(q)) / freq));
        }
        System.out.println("query weight " + m);//system 2
        double z = i - 1;//total documents
        while (iterator.hasNext()) {
            String word = iterator.next();
            // System.out.println("Value is : "+word+" "+ iterator.next()); 

            double df =0.0;
            try{
                df=dictionary.get(word).size();
            }catch(Exception e){
                continue;
            }
            System.out.println("df/doc freq: " + df);//document frequency for term

            double idf = Math.log(z / df) * 1.0;
            System.out.println("idf:" + idf);//inverse document frequency

            Integer[] arr = new Integer[dictionary.get(word).size()];
            arr = dictionary.get(word).toArray(arr);
            double nn = 0;
            for (Integer x : arr) {
                PackingListRow obj = new PackingListRow(word, x);
//x= doc id
                double n = items.get(obj) / ((total[x.intValue()] - 1) * 1.0 * idf);
                float f = (float) (items.get(obj) / ((total[x.intValue()] - 1) * 1.0 * idf));

                if (z >= 3) {
                    nn = 1000;
                } else {
                    nn = 1.0;
                }
                n = Math.abs(n * nn);//debug
           /* if(n>1){
                 rv.put(x, 1);
                 }else{
                 rv.put(x, 0);
                 }*/
                //useless maybe//tw.put(x, items.get(obj) / ((total[x.intValue()] - 1) * 1.0 * idf));//tf-idf scoring or ranking(tw)

                Double old = tw.get(x);
                tw.put(x, old == null ? n : n + old);

                tw2.put(x, n * m.get(word));//system 2
                System.out.println("term freq, tf: " + x + "=" + items.get(obj) / ((total[x.intValue()] - 1) * 1.0) + " " + n + " " + f);
            }

        }

        /*Integer[] ar = new Integer[tw.size()];
         ar = tw.getValue().toArray(ar);
        
         for (Integer x : ar) {
         double n=tw.get(x);
         if(n>1){
         rv.put(x, 1);
         }else{
         rv.put(x, 0);
         }
         }*/
        double r = 0, r2 = 0,t=1.0;
        double max = Collections.max(tw.values());
        if(max<1){
            t=max-.001;
        }
        Iterator hmIterator = tw.entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            double n = (double) mapElement.getValue();
            if (n > t) {
                rv.put((Integer) mapElement.getKey(), 1);
                r++;
            } else {
                rv.put((Integer) mapElement.getKey(), 0);
            }
        }
        if(r==0){
            
            double maxSet = Collections.max(tw.values());
            System.out.print("MAx"+maxSet);
        }

        //system2
        Iterator hmIterator2 = tw2.entrySet().iterator();

        while (hmIterator2.hasNext()) {
            Map.Entry mapElement2 = (Map.Entry) hmIterator2.next();
            double n2 = (double) mapElement2.getValue();
            if (n2 > 1) {
                rv2.put((Integer) mapElement2.getKey(), 1);//system2
                r2++;//total relevent 
            } else {
                rv2.put((Integer) mapElement2.getKey(), 0);//system2
            }
        }

        System.out.println("tw test" + tw);
        System.out.println("tw2 test" + tw2);//system 2
        System.out.println("rv1: " + rv);
        System.out.println("rv2: " + rv2);
        evaluate(rv);
        evaluate2(rv, r, rv2, r2, z);

        //termFrequency(st,dictionary);
        // romanticMovie(dictionary);
        // TODO code application logic here
    }

    private static String normalizeText(String st) throws FileNotFoundException {
        Scanner s = new Scanner(new File("stopwords.txt"));
        ArrayList<String> list2 = new ArrayList<String>();
        while (s.hasNext()) {
            list2.add(s.next());
        }
        s.close();
        List<String> allWords = new ArrayList<>(Arrays.asList(st.toLowerCase().split(" ")));
        allWords.removeAll(list2);
        String result = String.join(" ", allWords);
        return result; //To change body of generated methods, choose Tools | Templates.
    }

    private static Map<String, List<Integer>> Sort(Map<String, List<Integer>> dictionary) {

        List<Map.Entry<String, List<Integer>>> set = new LinkedList<Map.Entry<String, List<Integer>>>(dictionary.entrySet());

        // Sort the list 
        Collections.sort(set, new Comparator<Map.Entry<String, List<Integer>>>() {
            public int compare(Map.Entry<String, List<Integer>> o1,
                    Map.Entry<String, List<Integer>> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });

        // put data from sorted list to hashmap  
        HashMap<String, List<Integer>> dic = new LinkedHashMap<String, List<Integer>>();
        for (Map.Entry<String, List<Integer>> s : set) {
            dic.put(s.getKey(), s.getValue());
        }
        return dic;

    }

    private static void romanticMovie(Map<String, List<Integer>> dictionary) throws FileNotFoundException {

        Scanner rs = new Scanner(new File("romanticwords.txt"));
        ArrayList<String> rt = new ArrayList<String>();
        while (rs.hasNext()) {
            rt.add(rs.next());
        }
        rs.close();
        for (int i = 0; i < rt.size(); i++) {
            //List<Integer> l=new ArrayList<Integer>(dictionary.get(rt.get(i)));
            // int n=dictionary.get(rt.get(i)).size();
            System.out.println(dictionary.get(rt.get(i)));
        }

    }

    private static void addItem(Map<PackingListRow, Integer> items, PackingListRow packingListRow, int i) {

        Integer oldQuantity = items.get(packingListRow);
        items.put(packingListRow, oldQuantity == null ? i : i + oldQuantity);
    }

    private static void removeDuplicate(Map<String, List<Integer>> dictionary) {

        Set< Map.Entry< String, List<Integer>>> dic = dictionary.entrySet();

        for (Map.Entry< String, List<Integer>> e : dic) {
            List<Integer> m = new ArrayList<>(e.getValue());

            List<Integer> newList = m.stream().distinct().collect(Collectors.toList());

            dictionary.replace(e.getKey(), newList);

        }
    }

    private static void evaluate(HashMap<Integer, Integer> rv) {

        int totalDoc = rv.size();
        double arr[][] = new double[2][100];
        Set<Map.Entry<Integer, Integer>> set = rv.entrySet();
        int j = 0, rr = 0, i = 1;
        for (Map.Entry<Integer, Integer> me : set) {
            int m = me.getValue().intValue();
            if (me.getValue() == 1) {
                rr++;
            }

            arr[0][j] = (double) rr / i;
            arr[1][j] = (double) rr / totalDoc;
            System.out.println("doc" + me.getKey() + " " + arr[0][j] + " " + arr[1][j]);
            i++;
            j++;
        }
        for (int a = 0; a < j; a++) {
            System.out.println(arr[0][a]);

        }
        for (int a = 0; a < j; a++) {
            System.out.println(arr[1][a]);
        }

    }

    private static void evaluate2(HashMap<Integer, Integer> rv, double r, HashMap<Integer, Integer> rv2, double r2, double z) {

        double precision = r / rv.size();
        double precision2 = r2 / rv2.size();
        double recall = r / z;
        double recall2 = r2 / z;
        double f1 = 2.0 * (precision * recall) / (precision + recall);
        double f2 = 2.0 * (precision2 * recall2) / (precision2 + recall2);

        if (f1 > f2) {
            System.out.println("System 1 is better: " + f1 + "," + f2);
        } else {
            System.out.println("System 2 is better: " + f1 + "," + f2);
        }
        int totalDoc = rv.size();
        double arr[][] = new double[2][100];
        Set<Map.Entry<Integer, Integer>> set = rv.entrySet();
        int j = 0, rr = 0, i = 1;
        double d = 0;
        for (Map.Entry<Integer, Integer> me : set) {
            int m = me.getValue().intValue();
            if (me.getValue() == 1) {
                rr++;
                d += (double) rr / i;
            }

            arr[0][j] = (double) rr / i;  //perception
            arr[1][j] = (double) rr / totalDoc; //recall 
            System.out.println("doc" + me.getKey() + " " + arr[0][j] + " " + arr[1][j]);
            i++;
            j++;
        }
        
        Set<Map.Entry<Integer, Integer>> set2 = rv2.entrySet();
        j = 0;i = 1;
        int rr2 = 0; 
        double d2 = 0;
        for (Map.Entry<Integer, Integer> me2 : set2) {
            int m = me2.getValue().intValue();
            if (me2.getValue() == 1) {
                rr2++;
                d2 += (double) rr2 / i;
            }
            i++;
            j++;
        }


        for (int a = 0; a < j; a++) {
            System.out.println(arr[0][a]);

        }
        System.out.println("Perception avg " + (d / rr) + " " + rr);
        
        System.out.println("Perception avg " + (d2 / rr2) + " " + rr2);//system 2
        
        double map=((d/rr)+(d2/rr2))/2;
        System.out.println("map: "+map);
        for (int a = 0; a < j; a++) {
            System.out.println(arr[1][a]);
        }
    }

}

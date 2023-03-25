package application;

import dao.IDao;
import dao.Personne;
import dao.PersonneDao;
import metier.IMetier;
import metier.PersonneMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import presentation.IPersonneControleur;
import presentation.PersonneControleur;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Scanner;

public class SimulateurDage_App {
    static IPersonneControleur creditControleur;
    static Scanner clavier = new Scanner(System.in);

    private static boolean estUnNombre(String input) {
        try {
            Integer.parseInt(input);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public static void test1() {
        var dao = new PersonneDao();
        var metier = new PersonneMetier();
        var controleur = new PersonneControleur();
        //injection des dependances
        metier.setCreditDao(dao);
        controleur.setCreditMetier(metier);
        //tester
        String rep = "";
        do {
            System.out.println("=>[Test 1}Calcule de mensualité de crédit <=\n");
            try {
                String input = "";
                while (true) {
                    System.out.println("=> Entrez l'id du crédit :");
                    input = clavier.nextLine();
                    if (estUnNombre(input)) break;
                    System.out.println("Entree non valide!!!");
                }
                long id = Long.parseLong(input);
                controleur.afficher_Age(id);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Voulez vous quittez(oui / non)?");
            rep = clavier.nextLine();
        } while (!rep.equalsIgnoreCase("oui"));
        System.out.println("Au revoir ^_^");
    }
    public static void test2() throws Exception {

        String daoClass;
        String serviceClass;
        String controllerClass;
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream properttiesFile = classLoader.getResourceAsStream("config.properties");

        if (properttiesFile == null) throw new Exception("fichier config introuvable!!!");
        else {
            try {
                properties.load(properttiesFile);
                daoClass = properties.getProperty("DAO");
                serviceClass = properties.getProperty("SERVICE");
                controllerClass = properties.getProperty("CONTROLLER");
                properttiesFile.close();
            } catch (IOException e) {
                throw new Exception("Problème dee chargement des propriétés du fichier config");
            } finally {
                properties.clear();
            }
        }
        try {
            Class cDao = Class.forName(daoClass);
            Class cMetier = Class.forName(serviceClass);
            Class cContoleur = Class.forName(controllerClass);

            var dao = (IDao<Personne, Long>) cDao.getDeclaredConstructor().newInstance();
            var metier = (IMetier) cMetier.getDeclaredConstructor().newInstance();
            creditControleur = (IPersonneControleur) cContoleur.getDeclaredConstructor().newInstance();

            // dependecy injection
            Method setDao = cMetier.getMethod("setCreditDao", IDao.class);
            setDao.invoke(metier, dao);

            Method setMetier = cContoleur.getMethod("setCreditMetier", IMetier.class);
            setMetier.invoke(creditControleur, metier);

            creditControleur.afficher_Age(1L);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void test3() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-ioc.xml");
        creditControleur = (IPersonneControleur) context.getBean("controleur");
        creditControleur.afficher_Age(1L);
    }
    public static void test4() throws Exception{
        ApplicationContext context =new AnnotationConfigApplicationContext("dao","metier","presentation");
        creditControleur =(IPersonneControleur) context.getBean(IPersonneControleur.class);
        creditControleur.afficher_Age(1L);
        creditControleur.afficher_Age(2L);
    }
        public static void main(String[] args) throws Exception {
      //  test1();
       // test2();
      //  test3();
        test4();


            }
    }




package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class Main {
    // GAME OPTIONS
    public static final int DEFAULT_TEXT_SPEED = 35; // in milliseconds;
    public static final String KHAN_ACADEMY_LINKS_FILE = "khan_academy_links.xml"; // in milliseconds;

    public static final int OPTION_INTRODUCTION = 1;
    public static final int OPTION_PLAY = 2;
    public static final int OPTION_CREDITS = 3;
    public static final int OPTION_EXIT = 4;
    public static final int OPTION_BATTLE = 5;
    public static final int OPTION_BAG = 6;
    public static final int OPTION_TRAIN = 7;
    public static final int OPTION_SAVE = 8;
    public static final int OPTION_PLAY_EXIT = 9;
    public static final int OPTION_VALIDATE_CERTIFICATE = 10;
    public static final int OPTION_TRAIN_PA = 12;
    public static final int OPTION_TRAIN_PG = 13;
    public static final int OPTION_TRAIN_FIRST_DEGREE = 14;
    public static final int OPTION_TRAIN_SECOND_DEGREE = 15;
    public static final int OPTION_TRAIN_EXPONENTIAL = 16;
    public static final int OPTION_TRAIN_EXIT = 17;

    public static final Map<Integer, String> OPTIONS_INIT = new LinkedHashMap<>();
    static {
        OPTIONS_INIT.put(OPTION_INTRODUCTION, "Introdução");
        OPTIONS_INIT.put(OPTION_PLAY, "Jogar");
        OPTIONS_INIT.put(OPTION_CREDITS, "Créditos");
        OPTIONS_INIT.put(OPTION_EXIT, "Sair");
        OPTIONS_INIT.put(OPTION_VALIDATE_CERTIFICATE, "Validar Certificado");
    }
    public static final Map<Integer, String> OPTIONS_PLAY = new LinkedHashMap<>();
    static {
        OPTIONS_PLAY.put(OPTION_BATTLE, "Lutar em ginásios");
        OPTIONS_PLAY.put(OPTION_TRAIN, "Treinar");
        OPTIONS_PLAY.put(OPTION_BAG, "Mochila");
        OPTIONS_PLAY.put(OPTION_SAVE, "Salvar");
    }
    public static final Map<Integer, String> OPTIONS_SAVE = new LinkedHashMap<>();
    static {
        OPTIONS_SAVE.put(OPTION_PLAY, "Continuar Jogando");
        OPTIONS_SAVE.put(OPTION_PLAY_EXIT, "Sair");
    }
    public static final Map<Integer, String> OPTIONS_TRAIN = new LinkedHashMap<>();
    static {
        OPTIONS_TRAIN.put(OPTION_TRAIN_PA, "Prograssão Aritimética");
        OPTIONS_TRAIN.put(OPTION_TRAIN_PG, "Prograssão Geométrica");
        OPTIONS_TRAIN.put(OPTION_TRAIN_FIRST_DEGREE, "Funções de Primeiro Grau");
        OPTIONS_TRAIN.put(OPTION_TRAIN_SECOND_DEGREE, "Funções de Segundo Grau");
        OPTIONS_TRAIN.put(OPTION_TRAIN_EXPONENTIAL, "Funções Exponenciais");
        OPTIONS_TRAIN.put(OPTION_TRAIN_EXIT, "Sair");
    }

    // INPUTS
    public Scanner input = new Scanner(System.in);
    public String inputS = "";
    // USER DATA
    public String userUUID = "";
    public String userName = "";
    public String pokemon = "";
    public String[] badge = new String[5];
    // POKEMONS DATA
    public static final String[] STARTER_AVAILABLE_POKEMONS = new String[] {
            "Bulbassauro",
            "Charmander",
            "Squirtle",
    };
    // GYM DATA
    public static final String[] GYM_NAME = new String[] {
            "ginasio nutella x RAIZES",
            "ginasio santuario matematico",
            "ginasio numerico",
            "ginasio MTI",
            "ginasio Do Grão Mestre",
    };
    public static final String[] INSIGNIA = new String[] {
            "",
            "㊀",
            "㊁",
            "㊂",
            "㊃",
            "㊈",
    };
    public int valueWin = 0;

    // FALAS - USER
    public final String ASK_NAME = "Insira seu nome:";
    public final String NAME_CONFIRMATION = "Você tem certeza que o nome está correto? Ele será utilizado no seu certificado posteriormente!!\n(digite 'Sim' para confirmar).";
    public final String THANKS_FOR_PLAYING = "Obrigado por jogar!";
    public final String NO_NAME = "";
    // FALAS - POKEMONS
    public final String INTRODUCTION = "Bem vindx ao POKE-MATICA!!";
    public final String CHOOSE_POKEMON = "Escolha um pet(escreva o nome do pet):";
    public final String POKEMON_NOT_AVAILABLE = "Não temos esse pet escolha um entre as opções...";
    public final String NO_POKEMON = "";
    // FALAS - STORY
    public final String STORY_01 = "Na aldeia de Kaua, a matemática é usada para todas as coisas."
            + "Existe um grande campeonato de batalhas de matemática, onde cada participante leva um pet para a batalha, ao acertar o resultado da equação seu pet vence a rodada. Esse campeonato envolve 4 grandes mestres da aldeia."
            + "Os participantes têm que vencer cada um deles para avançar e enfrentar o melhor dos melhores. Sabendo disso, Kaua da escola local decide participar."
            + "Ao chegar no último ano da escola, Kaua terá que escolher um pet entre 3 para começar sua aventura.";
    public final String GYM_ASK = "Selecione o ginásio que quer enfrentar!";
    public final String WRONG_OPTION_MESSAGE = "Resposta errada, volte ao começo e tente novamente.";
    // FALAS - ERROR
    public final String ERROR_OPTION_NOT_AVAILABLE = "Escolha uma das opções válidas!";
    public final String ERROR_DEFAULT_MESSAGE = "Houve um erro, comunique nossa equipe ou tente novamente mais tarde. Obrigado!";
    public final String ERROR_NUMBER_FORMAT_EXCEPTION = "Caractere inserido é inválido!";

    /*------------------------------------------------------------------------------------------------*/
    /*---------------JWT AND CERTIFICATE AREA---------------JWT AND CERTIFICATE AREA------------------*/
    /*------------------------------------------------------------------------------------------------*/

    public final String ISSUER_CLAIM = "Grupo Poke-Matica";
    public final String SUBJECT_CLAIM = "Certificado Ginásio MTI";
    public final String NAME_CLAIM = "name";
    public final int EXPIRATION_CLAIM = 3;
    public final String SECRET_KEY_FILE_CLAIM = "poke-matica-secure-key.txt";

    public final String CERTIFICATE_NAME = "certificate.pdf";
    public final String CERTIFICATE_ABSOLUTE_PATH = System.getProperty("user.home") + "/Desktop/" + CERTIFICATE_NAME;
    public final String IMAGE_PATH = "images/img.png";
    public final String CERTIFICATE_TEXT_TITLE = "Certificado Poke-Matica";
    public final String CERTIFICATE_TEXT_PARAGRAPH = "Certificamos (não oficialmente) que %s concluiu o curso POKE-MATICA GINÁSIO DAS FUNCOES com sucesso no dia %d às %h";
    public final String CERTIFICATE_JWT_TEXT = "Token do certificado";

    public final Font TITLE_FONT = new Font(FontFamily.TIMES_ROMAN, 24, Font.BOLD);
    public final float TITLE_TO_TEXT_LINE_SPACING = 64f;
    public final Font TEXT_FONT = new Font(FontFamily.TIMES_ROMAN, 12);
    public final float TEXT_TO_TEXT_LINE_SPACING = 16f;
    public final Font JWT_FONT = new Font(FontFamily.TIMES_ROMAN, 10);
    public final float JWT_DROP_TO_BOTTOM = 256f;

    public Document certificate = new Document(PageSize.A4.rotate());;
    public PdfWriter certificateWriter;
    public PdfContentByte certificateImageCanvas;
    public Image certificateImageHandler;
    public Paragraph certificateParagraphHandler;
    /*------------------------------------------------------------------------------------------------*/
    /*---------------JWT AND CERTIFICATE AREA---------------JWT AND CERTIFICATE AREA------------------*/
    /*------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------------*/
    /*----------------XML AND SAVE GAME AREA-----------------XML AND SAVE GAME AREA-------------------*/
    /*------------------------------------------------------------------------------------------------*/

    public final String XML_SAVE_FILE_NAME = "save.xml";
    public final String XML_SAVE_ROOT_ELEMENT = "save";
    public final String XML_SAVE_MAIN_ELEMENT = "user";
    public final String XML_SAVE_MAIN_ELEMENT_NAME = "name";
    public final String XML_SAVE_MAIN_ELEMENT_UUID = "uuid";
    public final String XML_SAVE_MAIN_ELEMENT_BACKPACK = "backpack";
    public final String XML_SAVE_MAIN_ELEMENT_BACKPACK_POKEMON = "pokemon";
    public final String XML_SAVE_MAIN_ELEMENT_BACKPACK_BADGE = "badge";

    public org.w3c.dom.Document xmlDocBackup;

    public static final int MAKE_BACKUP = 100;
    public static final int GET_BACKUP = 101;

    public void xmlBackup(int type) throws Exception {
        DocumentBuilderFactory factoryBackup = DocumentBuilderFactory.newInstance();
        DocumentBuilder backupbuilder = factoryBackup.newDocumentBuilder();
        switch (type) {
            case MAKE_BACKUP:
                this.xmlDocBackup = backupbuilder.parse(XML_SAVE_FILE_NAME);
                break;
            case GET_BACKUP:
                xmlWriter(XML_SAVE_FILE_NAME, this.xmlDocBackup);
                break;
        }
    }

    // GAME
    public void preStartGame() {
        try {
            xmlBackup(MAKE_BACKUP);
            loadSaves();
            startGame();
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void startGame() {
        print("\n" + INTRODUCTION);
        handleOptions(OPTIONS_INIT);
    }

    // Handle the introduction, telling the game story and get user name
    public void handleIntroduction() {
        if (userName == NO_NAME) {
            print(ASK_NAME);
            userName = input.nextLine();

            print(NAME_CONFIRMATION);

            String yes = "sim";

            String validacao = input.nextLine();

            if (!validacao.equalsIgnoreCase(yes)) {
                print(ASK_NAME);
                userName = input.nextLine();
            }

        }
        clearScreen();
        divider();
        print(STORY_01.replaceAll("%s", userName));
        divider();

        if (pokemon == NO_POKEMON) {
            chooseInitialPokemon();
        }

        // After explain the story it auto-redirects to play mode
        handlePlay();
    }

    // Handle the play option, leading to certificate or lose
    public void handlePlay() {
        if (pokemon == NO_POKEMON || userName == NO_NAME) {
            handleIntroduction();
        }

        // System.out.println("HANDLE PLAY");
        handleOptions(OPTIONS_PLAY);
    }

    public void handleBattle() {
        int optionGym;
        print(GYM_ASK);
        divider();
        for (int i = 0; i < GYM_NAME.length; i++) {
            print((i + 1) + ")" + GYM_NAME[i]);
        }
        optionGym = inputInt();

        divider();
        selectGym(optionGym);

        handleOptions(OPTIONS_PLAY);
    }

    public void selectGym(int optionGym) {
        int optionDefault = 0;
        do {
            switch (optionGym) {
                case 1:
                    gymVeryEasy();
                    valueWin = 1;
                    break;
                case 2:
                    if (valueWin == 1) {
                        gymEasy();
                        valueWin = 2;
                        break;
                    }
                    print("Este ginásio ainda não foi desbloqueado...");
                    handleBattle();

                    break;

                case 3:
                    if (valueWin == 2) {
                        gymMedium();
                        valueWin = 3;
                    } else {
                        print("Este ginásio ainda não foi desbloqueado...");
                        handleBattle();
                    }

                    break;
                case 4:
                    if (valueWin == 3) {
                        gymHard();
                        valueWin = 4;
                    } else {
                        print("Este ginásio ainda não foi desbloqueado...");
                        handleBattle();
                    }

                    break;
                case 5:
                    if (valueWin == 3) {
                        print(
                                "Parabéns por ter vencer os 5 ginásios e ter se tornando um mestre Poke-Matica!");
                        valueWin = 5;
                        handleCertificateCreator();
                    } else {
                        print("Este ginásio ainda não foi desbloqueado...");
                        handleBattle();
                    }

                    break;
                default:
                    print("Opção inválida, tente novamente.");
                    optionDefault = 1;
                    break;
            }
        } while (optionDefault == 1);
    }

    public void gymVeryEasy() {
        String leaderName = "Mestre Executor de Raízes";
        print(
                "Seja BEM VINDX ao " + GYM_NAME[0]
                        + "! Eu sou o " + leaderName
                        + ", lider deste ginásio. Ganhe de mim em uma batalha Poke-Mática e receba sua insígnia\n");

        print(
                leaderName
                        + ": Então, é sobre você que estão falando na aldeia? Acha mesmo que tem a coragem para desafiar grandes Mestres Poke-Mática?\n");

        print(userName + ": Sim! Prazer, me chamo " + userName + ", e vim derotar vocês!\n");

        print(leaderName +
                ": Então vamos logo para sua primeira batalha, já que está tão confiante assim, estes são os meus pokémons!!\n");

        print("\n\n     Multiplicador de Socos\n    Divisor de Chamas\n     Soma Solar\n");

        questionPG();

        print("Parabéns, " + userName
                + " você venceu sua primeira batalha. Garantirei que não será assim nas próximas...\n");

        divider();
    }

    public void gymEasy() {
        String leaderName = "Sensei Matemático";
        print(
                "Seja BEM VINDX ao " + GYM_NAME[1]
                        + "! Você chegou ao segundo ginasio, ganhe a batalha matemática e receba uma insígnia\n");
        divider();

        print(leaderName + ": Todos estão comentado sobre você, agora que derotou o mestre do ginásio " + GYM_NAME[0]
                + " \n ");

        print(userName
                + " : Fiquei sabendo dos comentários, mas não acho que seja para tanto. O primeiro mestre foi muito facil de passar!\n");

        print(leaderName + ": Oloco, então você se acha muito mesmo bom né? Sendo assim, vamos batalhar.\n ");

        print("\n       Subtramon\n     Elektro Bhaskara ");

        questionPA();

        print("...\nMuito bem, a partir de agora as coisas vão ficar ainda mais dificeis a cada vitória.\n");

        divider();
    }

    public void gymMedium() {
        String leaderName = "Mestre dos Números";
        print(
                "Seja BEM VINDX ao " + GYM_NAME[2]
                        + "! Você chegou ao terceiro ginásio, ganhe a batalha matemática e receba uma insígnia\n");
        divider();

        print(leaderName + ": " + userName
                + " né? Fiquei sabendo que você já derrotou dois metres Poke-Mática da aldeia...\n");

        print("Me chamo Mestre dos numeros, vamos batalhar.\n");

        print("\n       Gyarados Romanos\n      Potencia Hipnóptica\n");

        questionFuncOne();

        print(leaderName
                + ": Você está indo muito bem, já derrotou 3 grandes mestres Poke-Mática... Acho que não irá além disso.\n");

        divider();
    }

    public void gymHard() {
        String leaderName = "";
        print(
                "Seja BEM VINDX ao " + GYM_NAME[3]
                        + "! Você chegou ao quarto ginásio, ganhe essa batalha Poke-Mática e receba uma insígnia\n");
        divider();

        print(leaderName
                + ": Poucas pessoas chegaram a este nivel, nenhuma nunca passou.\nVocê tem um grande potencial, mas sua sorte não será o bastante aqui!\n");

        print("\n       Número Fantasma\n       Rocha Binária\n");

        questionFuncsecond();

        print("Agora você está na final, então se prepare, seu próximo adversário será o Grão Mestre, e o seu pet Lendário\n");

        divider();
    }

    public void questionPG() {
        int validate = 0;
        ArrayList<Integer> prohibited = new ArrayList<Integer>();
        int randomNumber;
        do {
            if (prohibited.size() != 3) {
                randomNumber = sortInt(0, 3, prohibited);
                prohibited.add(randomNumber);

                switch (randomNumber) {
                    case 0:
                        validate = questionPG_1();
                        break;
                    case 1:
                        validate = questionPG_2();
                        break;
                    case 2:
                        validate = questionPG_3();
                        break;
                }
            } else {
                validate = 2;
            }

        } while (!(validate == 1 || validate == 2));
        if (validate == 1) {
            handleOptions(OPTIONS_PLAY);

        }
    }

    public int sortInt(int min, int max, ArrayList<Integer> prohibited) {
        int r = (int) ((Math.random() * (max - min)) + min);
        for (int i : prohibited) {
            if (r == i) {
                r = sortInt(min, max, prohibited);
            }
        }
        return r;
    }

    public int questionPG_1() {
        int result;
        // question 1
        print("Qual é o proximo termo da Progressão geométrica:\n 3,6,12,...");
        divider();
        print("1)25\n2)12\n3)34\n4)24\n5)254");
        result = inputInt();
        divider();

        if (result != 4) {
            print(WRONG_OPTION_MESSAGE);
            divider();
            return 1;
        } else {
            return 0;
        }
    }

    public int questionPG_2() {
        int result;
        // question 2
        print("Qual é o proximo termo da Progressão geométrica:\n 192,48,12,...");
        divider();
        print("1)5\n2)122\n3)4\n4)14\n5)3");
        result = inputInt();
        divider();
        if (result != 5) {
            print(WRONG_OPTION_MESSAGE);
            divider();
            return 1;
        } else {
            return 0;
        }
    }

    public int questionPG_3() {
        int result;
        // question 3
        print("Qual é o 5º termo na Progressão:\n b(n)=-1()2^(n-1)");
        divider();
        print("1)-15\n2)-16\n3)15\n4)24\n5)-254");
        result = inputInt();
        divider();
        if (result != 2) {
            print(WRONG_OPTION_MESSAGE);
            divider();
            return 1;
        } else {
            return 0;
        }
    }

    public void questionPA() {
        int validate = 0, result = 0;
        do {
            // question 1
            print("\nComplete a formula recursiva de g(n)");
            divider();
            print("g(n) = 25-49(n-1)");
            print("g(1)=????");
            print("g(n) = g(n-1)+?????");
            divider();
            print("1)25 e -49 \n2)23 e -25 \n3)-49 e 25 \n4)32 e -25 \n5)32 e -23");
            result = inputInt();
            divider();
            if (result != 1) {
                print(WRONG_OPTION_MESSAGE);
                divider();
                validate = 1;
                break;
            }
            // question 2
            print("Complete a formula recursiva de g(n)");
            divider();
            print("g(n) = 1+5(n-1)");
            print("g(1)=????");
            print("g(n) = g(n-1)+?????");
            divider();
            print("1)1 e 5 \n2)-1 e -5 \n3)-3 e 5 \n4)3 e -2 \n5)3 e -2");
            result = inputInt();
            divider();
            if (result != 1) {
                print(WRONG_OPTION_MESSAGE);
                divider();
                validate = 1;
                break;
            }

            validate = 2;

        } while (!(validate == 1 || validate == 2));
        if (validate == 1) {
            handleOptions(OPTIONS_PLAY);

        }
    }

    public void questionFuncOne() {
        int validate = 0, result = 0;
        do {
            // question 1
            print("\nDescubra o valode de h(-18)=???");
            print("sabendo que h(x)= 17 + x/6");
            divider();
            print("1)25 \n2)-14 \n3)-49 \n4)32 \n5)14");
            result = inputInt();
            divider();
            if (result != 5) {
                print("resposta errada, volte ao começo e tente novamente");
                divider();
                validate = 1;
                break;
            }
            // question 2
            print("\nDescubra o valode de f(30)=???");
            print("sabendo que f(x)= -14 - 0,05x");
            divider();
            print("1)23 \n2)-2 \n3)-11 \n4)-1 \n5)1");
            result = inputInt();
            divider();
            if (result != 4) {
                print(WRONG_OPTION_MESSAGE);
                divider();
                validate = 1;
                break;
            }

            validate = 2;

        } while (!(validate == 1 || validate == 2));
        if (validate == 1) {
            handleOptions(OPTIONS_PLAY);
        }
    }

    public void questionFuncsecond() {
        int validate = 0, result = 0;
        do {
            // question 1
            print("\nEncontre as raizes da função");
            print("Insira as soluções da menor para a maior");
            divider();
            print("f(x) = (-x-2)(-2x-3)");
            divider();
            print("1)Menor= -4 Maior = -1/2 \n2)Menor= -2 Maior= -3/2 \n3)Menor=-1 Maior= 1 \n4)Menor= 4 Maior= 10 \n5)Menor= 1 Maior= 3");
            result = inputInt();
            divider();
            if (result != 2) {
                print(WRONG_OPTION_MESSAGE);
                divider();
                validate = 1;
                break;
            }
            // question 2
            print("\nEncontre as raizes da função");
            print("Insira as soluções da menor para a maior");
            divider();
            print("(2x+4)(3x-2)=0");
            divider();
            print("1)Menor= -2 Maior = 2/3 \n2)Menor= -2 Maior= 3/2 \n3)Menor=-2 Maior= 4 \n4)Menor= -2/3 Maior= 1 \n5)Menor= 12 Maior= 21");
            result = inputInt();
            divider();
            if (result != 1) {
                print(WRONG_OPTION_MESSAGE);
                divider();
                validate = 1;
                break;
            }

            validate = 2;

        } while (!(validate == 1 || validate == 2));
        if (validate == 1) {
            handleOptions(OPTIONS_PLAY);

        }
    }

    public void handleTrain() {
        int selectedOption = handleOptions(OPTIONS_TRAIN);

        if (selectedOption == OPTION_TRAIN_EXIT) {
            handleOptions(OPTIONS_PLAY);
        } else {
            try {
                train(selectedOption);
            } catch (Exception e) {
                handleError(e);
            }
            handleTrain();
        }
    }

    public void handleBag() {
        print("Insignias: ");mostrarInsignias(valueWin);
        print("Pokemons");
        handleOptions(OPTIONS_PLAY);
    }
    public void mostrarInsignias(int valueWin){
        for (int i = 0; i < valueWin; i++) {
            print(INSIGNIA[i]);
        }
    }

    public void handleSave() {
        // System.out.println("handle save");
        saveGame();
        handleOptions(OPTIONS_SAVE);
    }

    // Handle the Credits option, showing the creator names and references
    public void handleCredits() {
        divider();
        System.out.println("Créditos:");
        System.out.println(
                "\nDesenvolvedores:" +
                        "\n   Guilherme Rojas Thomazini" +
                        "\n   João Victor Carvalho dos Santos" +
                        "\n   Kauã Chaves Calixto" +
                        "\n   Lucas Gabriel Pereira" +
                        "\n" +
                        "\nAgradecimentos Especiais:" +
                        "\n   Professor Marcio Welker Correa" +
                        "" +
                        "");
        divider();

        handleOptions(OPTIONS_INIT);
    }

    // Handle the Exit option
    public void handleExit() {
        print(THANKS_FOR_PLAYING);
        // System.exit(0) indicates successful termination
        System.exit(0);
    }

    // Handle the certificate emission
    public void handleCertificateValidation() {
        String token = askForToken();
        Jws<Claims> jws = decodeJWT(token);
        Claims body = jws.getBody();

        // Printing status
        divider();
        Date now = new Date(System.currentTimeMillis());
        Date exp = body.getExpiration();
        if (now.getTime() > exp.getTime()) {
            System.out.println("Status Atual: Expirado!");
        } else {
            System.out.println("Status Atual: Ativo!");
        }
        System.out.println();
        System.out.println("Emitido por:        " + body.getIssuer());
        System.out.println("Assunto:            " + body.getSubject());
        System.out.println("Emitido em:         " + body.getIssuedAt());
        System.out.println("Data de exipração:  " + body.getExpiration());
        System.out.println();
        System.out.println("Dono:               " + body.get(NAME_CLAIM));
        divider();

        // Callback options
        print("Pressione ENTER para sair");
        input.nextLine();
        clearScreen();
        handleOptions(OPTIONS_INIT);
    }

    // Handle all code errors, treating then and avoiding Crashes
    public void handleError(Exception e) {
        try {
            throw e;
        } catch (NumberFormatException nfe) {
            System.out.println(ERROR_NUMBER_FORMAT_EXCEPTION);
        } catch (Exception e1) {
            try {
                xmlBackup(GET_BACKUP);
            } catch (Exception e2) {
                System.out.println(ERROR_DEFAULT_MESSAGE);
            }
            System.out.println(ERROR_DEFAULT_MESSAGE);
            // System.out.println("Erro: " + e);
        }
    }

    /*------------------------------------------------------------------------------------------------*/

    // UTILS
    public void print(String str) {
        char[] arr = str.toCharArray();
        int milisToAdd = DEFAULT_TEXT_SPEED;

        int pos = 0;
        long currentTime = System.currentTimeMillis();
        long calcTime = currentTime + milisToAdd;
        long targetTime = currentTime + (milisToAdd * arr.length);

        do {
            currentTime = System.currentTimeMillis();
            if (currentTime >= calcTime) {
                System.out.print(arr[pos]);
                pos++;
                calcTime += milisToAdd;
            }
        } while (calcTime <= targetTime);
        System.out.println();
    }

    public void speedPrint(String str) {
        char[] arr = str.toCharArray();
        int milisToAdd = (int) (DEFAULT_TEXT_SPEED * 0.5);

        int pos = 0;
        long currentTime = System.currentTimeMillis();
        long calcTime = currentTime + milisToAdd;
        long targetTime = currentTime + (milisToAdd * arr.length);

        do {
            currentTime = System.currentTimeMillis();
            if (currentTime >= calcTime) {
                System.out.print(arr[pos]);
                pos++;
                calcTime += milisToAdd;
            }
        } while (calcTime <= targetTime);
        System.out.println();
    }

    public void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public void divider() {
        System.out.println("----------------------------------------/");
    }

    public boolean stringArrayContains(String[] arr, String value) {
        for (String string : arr) {
            if (string.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    // Ask user what pokemon they want
    public String chooseInitialPokemon() {
        print(CHOOSE_POKEMON);
        divider();
        for (String pokemonName : STARTER_AVAILABLE_POKEMONS) {
            print(pokemonName);
        }
        divider();

        pokemon = input.nextLine();

        // validating pokemon
        if (stringArrayContains(STARTER_AVAILABLE_POKEMONS, pokemon)) {
            return pokemon;
        } else {
            // If pokemon is not available it calls the func again
            print(POKEMON_NOT_AVAILABLE);
            pokemon = chooseInitialPokemon();
        }
        return pokemon;
    }

    public int inputInt() {
        int i;
        i = input.nextInt();
        return i;
    }

    // Ask user what they want to do
    public int getSelectedOption(Map<Integer, String> options) {
        // Converting from Map to int Array with available options
        Set<Integer> keys = options.keySet();
        int[] optionsArr = new int[keys.size()];
        int index = 0;
        for (Integer element : keys) {
            optionsArr[index++] = element.intValue();
        }

        // int[] optionsArr = options.keySet().stream().mapToInt(i -> i).toArray();
        int selectedOption = 0;

        // Printing options
        divider();
        for (int i = 1; i <= optionsArr.length; i++) {
            System.out.println(i + ") " + options.get(optionsArr[i - 1]));
        }
        divider();

        // Getting selected option
        inputS = input.nextLine();

        // handle input
        try {
            selectedOption = Integer.parseInt(inputS) - 1;
            return optionsArr[selectedOption];
            // If any errors occurred, like invalid number
        } catch (Exception e) {
            print(ERROR_OPTION_NOT_AVAILABLE);
            // Looping through this function again
            int key = getSelectedOption(options);
            for (int i = 0; i < optionsArr.length; i++) {
                // If the option key == optionArray at this position
                if (key == optionsArr[i]) {
                    // set this position to be returned, because it's the key place
                    selectedOption = i;
                }
            }
        }
        return optionsArr[selectedOption];
    }

    // Calls getSelectedOption and redirects to the function selected
    public int handleOptions(Map<Integer, String> options) {
        // Getting selected option between options
        int selectedOption = getSelectedOption(options);
        switch (selectedOption) {
            case OPTION_INTRODUCTION:
                handleIntroduction();
                break;
            case OPTION_PLAY:
                handlePlay();
                break;
            case OPTION_CREDITS:
                handleCredits();
                break;
            case OPTION_EXIT:
                handleExit();
                break;
            case OPTION_TRAIN:
                handleTrain();
                break;
            case OPTION_BAG:
                handleBag();
                break;
            case OPTION_SAVE:
                handleSave();
                break;
            case OPTION_BATTLE:
                handleBattle();
                break;
            case OPTION_PLAY_EXIT:
                startGame();
                break;
            case OPTION_VALIDATE_CERTIFICATE:
                handleCertificateValidation();
                break;
        }

        return selectedOption;
    }

    // JWT HANDLERS
    public String generateJWT() {
        String jwtToken = Jwts.builder()
                .setIssuer(ISSUER_CLAIM)
                .setSubject(SUBJECT_CLAIM)
                .claim(NAME_CLAIM, userName)
                .setIssuedAt(new Date())
                .setExpiration(
                        Date.from(
                                LocalDateTime.now().plusMonths(EXPIRATION_CLAIM)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                .signWith(getSecretKey())
                .compact();

        return jwtToken;
    }

    public SecretKey getSecretKey() {
        String poke_matica_secret_key = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(SECRET_KEY_FILE_CLAIM))) {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }
            poke_matica_secret_key = sb.toString();
        } catch (Exception e) {
            handleError(e);
        }
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(poke_matica_secret_key));
    }

    public Jws<Claims> decodeJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
    }

    public String askForToken() {
        speedPrint("Insira o Token do certificado:");
        String token = input.nextLine();
        // cleanning illegal character
        token = token.replace(" ", "");
        if (!isValidToken(token)) {
            speedPrint("\nToken inválido, por favor tente novamente.");
            return askForToken();
        } else {
            return token;
        }
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //
    // XML HANDLERS
    public void xmlWriter(String fileName, org.w3c.dom.Document xmlDoc) throws Exception {
        // Output Only
        DOMSource domSource = new DOMSource(xmlDoc);
        File file = new File(fileName);
        Result result = new StreamResult(file);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // Transformer transformer = transformerFactory.newTransformer(new
        // StreamSource("strip-space.xsl"));
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
        transformer.transform(domSource, result);

        xmlBackup(MAKE_BACKUP);
    }

    public void train(int TYPE) throws Exception {
        final String QUERY_TOPIC = "topic";
        final String QUERY_ID = "id";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(KHAN_ACADEMY_LINKS_FILE);
        NodeList topic_list = doc.getElementsByTagName(QUERY_TOPIC);

        for (int i = 0; i < topic_list.getLength(); i++) {
            Node item = topic_list.item(i);

            if (item.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (org.w3c.dom.Element) item;
                String id = element.getAttribute(QUERY_ID);
                // Comparing if it's the user selected options
                if (id.equalsIgnoreCase(TYPE + "")) {
                    NodeList data = element.getChildNodes();
                    for (int j = 0; j < data.getLength(); j++) {
                        Node valueWrapper = data.item(j);
                        if (valueWrapper.getNodeType() == Node.ELEMENT_NODE) {
                            org.w3c.dom.Element value = (org.w3c.dom.Element) valueWrapper;
                            String str = String.valueOf(value.getTextContent());

                            System.out.print(str);
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    public void loadSaves() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(XML_SAVE_FILE_NAME);

        NodeList user_list = doc.getElementsByTagName(XML_SAVE_MAIN_ELEMENT);

        if (user_list.getLength() > 0) {
            for (int i = 0; i < user_list.getLength(); i++) {
                Node item = user_list.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) item;
                    NodeList data = element.getChildNodes();
                    String lastLogin = element.getAttribute("date");

                    System.out.println("SAVE " + (i + 1) + " " + lastLogin);

                    for (int j = 0; j < data.getLength(); j++) {
                        Node valueWrapper = data.item(j);
                        if (valueWrapper.getNodeType() == Node.ELEMENT_NODE) {
                            org.w3c.dom.Element value = (org.w3c.dom.Element) valueWrapper;

                            String str = value.getTextContent().replace("\n", "");
                            switch (value.getNodeName()) {
                                case "name":
                                    System.out.println("   NOME - " + str);
                                    break;
                                case "backpack":
                                    System.out.println("   MOCHILA:");
                                    NodeList list = value.getChildNodes();
                                    for (int k = 0; k < list.getLength(); k++) {
                                        Node e = list.item(k);
                                        if (e.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element v = (org.w3c.dom.Element) e;
                                            switch (v.getNodeName()) {
                                                case "pokemon":
                                                    System.out.println(
                                                            "      Pokemons:\n           " + v.getTextContent());
                                                    break;
                                                case "badge":
                                                    System.out.println(
                                                            "      Insígnias:\n          " + v.getTextContent());

                                                    break;
                                            }
                                        }
                                    }
                            }
                        }
                    }
                    divider();
                }
            }
            int save = askSave() - 1;
            if (save == -1) {
                return;
            }
            Node user = user_list.item(save);

            if (user.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (org.w3c.dom.Element) user;
                NodeList data = element.getChildNodes();

                for (int j = 0; j < data.getLength(); j++) {
                    Node valueWrapper = data.item(j);
                    if (valueWrapper.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element value = (org.w3c.dom.Element) valueWrapper;

                        String str = value.getTextContent().replace("\n", "");

                        switch (value.getNodeName()) {
                            case "name":
                                this.userName = str;
                                break;
                            case "uuid":
                                this.userUUID = str;
                                break;
                            case "backpack":
                                NodeList list = value.getChildNodes();
                                for (int k = 0; k < list.getLength(); k++) {
                                    Node e = list.item(k);
                                    if (e.getNodeType() == Node.ELEMENT_NODE) {
                                        org.w3c.dom.Element v = (org.w3c.dom.Element) e;
                                        int badges = 0;
                                        switch (v.getNodeName()) {
                                            case "pokemon":
                                                this.pokemon = v.getTextContent();
                                                break;
                                            case "badge":
                                                this.badge[badges] = v.getTextContent();
                                                badges++;
                                                break;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
                divider();
            }
            System.out.println(userName);
            System.out.println(userUUID);
            System.out.println(pokemon);
            System.out.println(badge[0] + ", " + badge[1] + ", " + badge[2] + ", " + badge[3] + ", " + badge[4]);
            divider();
        }

    }

    public int askSave() {
        try {
            speedPrint("Escolha o seu save, ou digite '0' para criar um save novo!");
            int save = Integer.parseInt(input.nextLine().charAt(0) + "");
            return save;
        } catch (Exception e) {
            return askSave();
        }
    }

    public void saveGame() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(XML_SAVE_FILE_NAME);
            if (userUUID == "") {
                addUser(doc);
            } else {
                updateUser(doc);
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void addUser(org.w3c.dom.Document xmlDoc) throws Exception {
        org.w3c.dom.Element user, userName, userUUID, userBackpack, backpackPokemon,
                backpackBadge;

        // Initialize
        user = xmlDoc.createElement(XML_SAVE_MAIN_ELEMENT);
        userName = xmlDoc.createElement(XML_SAVE_MAIN_ELEMENT_NAME);
        userUUID = xmlDoc.createElement(XML_SAVE_MAIN_ELEMENT_UUID);
        userBackpack = xmlDoc.createElement(XML_SAVE_MAIN_ELEMENT_BACKPACK);
        backpackPokemon = xmlDoc.createElement(XML_SAVE_MAIN_ELEMENT_BACKPACK_POKEMON);
        backpackBadge = xmlDoc.createElement(XML_SAVE_MAIN_ELEMENT_BACKPACK_BADGE);

        // UserName
        Text userNameText = xmlDoc.createTextNode(this.userName);
        userName.appendChild(userNameText);
        // UserUUID
        UUID uuid = UUID.randomUUID();
        Text userUUIDText = xmlDoc.createTextNode(uuid + "");
        userUUID.appendChild(userUUIDText);
        // UserBackpack
        // Pokemon
        Text pokemonText = xmlDoc.createTextNode(this.pokemon);
        backpackPokemon.appendChild(pokemonText);
        // Badge
        for (String badg : badge) {
            Text badgeText = xmlDoc.createTextNode(badg);
            backpackBadge.appendChild(badgeText);
        }
        userBackpack.appendChild(backpackPokemon);
        userBackpack.appendChild(backpackBadge);

        // Appending
        user.appendChild(userUUID);
        user.appendChild(userName);
        user.appendChild(userBackpack);

        SimpleDateFormat CERTIFICATE_DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy");
        SimpleDateFormat CERTIFICATE_HOUR_FORMAT = new SimpleDateFormat("HH:mm");
        String dateNow = CERTIFICATE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        String hourNow = CERTIFICATE_HOUR_FORMAT.format(new Date(System.currentTimeMillis()));
        user.setAttribute("date", dateNow + " " + hourNow);

        xmlDoc.getFirstChild().appendChild(user);

        xmlWriter(XML_SAVE_FILE_NAME, xmlDoc);
        System.out.println("Usuário " + this.userName + " adicionado!");
    }

    public void updateUser(org.w3c.dom.Document xmlDoc) throws Exception {
        NodeList user_list = xmlDoc.getElementsByTagName(XML_SAVE_MAIN_ELEMENT);
        String uuid = "";

        if (user_list.getLength() > 0) {
            for (int i = 0; i < user_list.getLength(); i++) {
                Node item = user_list.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) item;
                    NodeList data = element.getChildNodes();
                    for (int j = 0; j < data.getLength(); j++) {
                        Node valueWrapper = data.item(j);
                        if (valueWrapper.getNodeType() == Node.ELEMENT_NODE) {
                            org.w3c.dom.Element value = (org.w3c.dom.Element) valueWrapper;

                            if (value.getNodeName() == "uuid") {
                                String str = value.getTextContent().replace("\n", "");
                                uuid = str;
                            }

                            if (uuid.equals(this.userUUID)) {
                                switch (value.getNodeName()) {
                                    case "backpack":
                                        NodeList list = value.getChildNodes();
                                        for (int k = 0; k < list.getLength(); k++) {
                                            Node e = list.item(k);
                                            if (e.getNodeType() == Node.ELEMENT_NODE) {
                                                org.w3c.dom.Element v = (org.w3c.dom.Element) e;
                                                int badges = 0;
                                                switch (v.getNodeName()) {
                                                    case "pokemon":
                                                        v.setTextContent(this.pokemon);
                                                        break;
                                                    case "badge":
                                                        v.setTextContent(this.badge[badges]);
                                                        badges++;
                                                        break;
                                                }
                                            }
                                        }
                                }
                                xmlWriter(XML_SAVE_FILE_NAME, xmlDoc);
                            }

                        }
                    }
                }
            }
        }

    }

    //
    // PDF HANDLERS
    public void handleCertificateCreator() {
        try {
            openDocument();

            certificateVariablesInit();
            fillCertificateValues();

            closeDocument();

            print(
                    "Certificado Emitido com sucesso!\n" + CERTIFICATE_ABSOLUTE_PATH + "\n");
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void openDocument() throws Exception {
        certificateWriter = PdfWriter.getInstance(certificate, new FileOutputStream(CERTIFICATE_ABSOLUTE_PATH));
        certificate.open();
    }

    public void closeDocument() throws Exception {
        certificate.close();
    }

    public void certificateVariablesInit() throws Exception {
        certificateImageCanvas = certificateWriter.getDirectContentUnder();
    }

    public void fillCertificateValues() throws Exception {
        // TITLE
        certificateParagraphHandler = new Paragraph(CERTIFICATE_TEXT_TITLE, TITLE_FONT);
        certificateParagraphHandler.setAlignment(Element.ALIGN_CENTER);
        certificate.add(certificateParagraphHandler);
        // Text - SKIP LINE
        certificateLineSpace(TITLE_TO_TEXT_LINE_SPACING);
        // Text
        SimpleDateFormat CERTIFICATE_DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy");
        SimpleDateFormat CERTIFICATE_HOUR_FORMAT = new SimpleDateFormat("HH:mm");
        String dateNow = CERTIFICATE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        String hourNow = CERTIFICATE_HOUR_FORMAT.format(new Date(System.currentTimeMillis()));

        certificateParagraphHandler = new Paragraph(
                CERTIFICATE_TEXT_PARAGRAPH.replace("%s", userName).replace("%d", dateNow).replace("%h", hourNow),
                TEXT_FONT);
        certificateParagraphHandler.setAlignment(Element.ALIGN_CENTER);
        certificate.add(certificateParagraphHandler);
        // // Image
        certificateAddImageInCenter(IMAGE_PATH);
        // Text - SKIP LINE
        certificateLineSpace(JWT_DROP_TO_BOTTOM);
        // Text
        certificateParagraphHandler = new Paragraph(CERTIFICATE_JWT_TEXT, TEXT_FONT);
        certificateParagraphHandler.setAlignment(Element.ALIGN_CENTER);
        certificate.add(certificateParagraphHandler);
        // Text - SKIP LINE
        certificateLineSpace(TEXT_TO_TEXT_LINE_SPACING);
        // Text
        certificateParagraphHandler = new Paragraph(generateJWT(), JWT_FONT);
        certificateParagraphHandler.setAlignment(Element.ALIGN_CENTER);
        certificate.add(certificateParagraphHandler);
    }

    public void certificateLineSpace(Float type) throws DocumentException {
        certificateParagraphHandler = new Paragraph();
        certificateParagraphHandler.setAlignment(Element.ALIGN_CENTER);
        certificateParagraphHandler.setSpacingAfter(type);
        certificate.add(certificateParagraphHandler);
    }

    public void certificateAddImageInCenter(String path) throws Exception {
        certificateImageHandler = Image.getInstance(path);
        certificateImageHandler.scaleToFit(64, 64);
        float imageCenterX = (PageSize.A4.rotate().getWidth() / 2) - (64 / 2);
        float imageCenterY = (PageSize.A4.rotate().getHeight() / 2) - (64 / 2);
        certificateImageHandler.setAbsolutePosition(imageCenterX, imageCenterY);
        certificateImageCanvas.addImage(certificateImageHandler);
    }

    //
    public static void main(String args[]) {
        Main main = new Main();
        main.preStartGame();
    }
}

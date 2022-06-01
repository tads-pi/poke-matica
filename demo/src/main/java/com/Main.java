package com;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.crypto.SecretKey;

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
import javafx.scene.input.PickResult;

public class Main {
    // GAME OPTIONS
    public static final int DEFAULT_TEXT_SPEED = 35; // in milliseconds;

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

    public static final Map<Integer, String> OPTIONS_INIT = new HashMap<>();
    static {
        OPTIONS_INIT.put(OPTION_INTRODUCTION, "Introdução");
        OPTIONS_INIT.put(OPTION_PLAY, "Jogar");
        OPTIONS_INIT.put(OPTION_CREDITS, "Créditos");
        OPTIONS_INIT.put(OPTION_EXIT, "Sair");
        OPTIONS_INIT.put(OPTION_VALIDATE_CERTIFICATE, "Validar Certificado");
    }
    public static final Map<Integer, String> OPTIONS_PLAY = new HashMap<>();
    static {
        OPTIONS_PLAY.put(OPTION_BATTLE, "Lutar em ginásios");
        OPTIONS_PLAY.put(OPTION_TRAIN, "Treinar");
        OPTIONS_PLAY.put(OPTION_BAG, "Mochila");
        OPTIONS_PLAY.put(OPTION_SAVE, "Salvar");
    }
    public static final Map<Integer, String> OPTIONS_SAVE = new HashMap<>();
    static {
        OPTIONS_SAVE.put(OPTION_PLAY, "Continuar Jogando");
        OPTIONS_SAVE.put(OPTION_PLAY_EXIT, "Sair");
    }

    // INPUTS
    public Scanner input = new Scanner(System.in);
    public String inputS = "";
    // USER DATA
    public String userName = "";
    public String pokemon = "";
    // POKEMONS DATA
    public static final String[] STARTER_AVAILABLE_POKEMONS = new String[] {
            "Bulbassauro",
            "Charmander",
            "Squirtle",
            "1"
    };
    //GYM DATA
    public static final String[] GYM_NAME = new String[] {
        "01)ginasio1",
        "02)ginasio2",
        "03)ginasio3",
        "04)ginasio4",
        "05)ginasio MTI",
    };

    // FALAS - USER
    public final String ASK_NAME = "insira seu nome:";
    public final String THANKS_FOR_PLAYING = "Obrigado por jogar!";
    public final String NO_NAME = "";
    // FALAS - POKEMONS
    public final String INTRODUCTION = "introdução para escolha de opções aqui";
    public final String CHOOSE_POKEMON = "escolha um pet(escreva o nome do pet):";
    public final String POKEMON_NOT_AVAILABLE = "não temos esse pet escolha um entre as opções...";
    public final String POKEMON_CHALLENGE_CALL = "opa antes de de escolher %p, você precisa passar por um desafio!";
    public final String NO_POKEMON = "";
    // FALAS - STORY
    public final String STORY_01 = "Em uma aldeia onde %s mora a matemática é usada para todas as coisas.";
    public final String STORY_02 = "Existe um grande campeonato de batalhas de matemática onde cada participantes leva um pet para a batalha ... ";
    public final String STORY_03 = "e ao acertar o resultado da conta o seu pet pode atacar o pet do adversário Esse campeonato envolve 10 anciões da aldeia muito bons em matemática...";
    public final String STORY_04 = "Os participantes tem que vencer cada um deles para avançar e enfrentar o melhor dos melhores Sabendo disso %s da escola local que se considera muito bom em matemática decidiu participar.";
    public final String STORY_05 = "Ao chegar no ultimo ano da escola %s terá que escolher um pet entre 3 para começar sua aventura.";
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
    public final String CERTIFICATE_TEXT_TITLE = "TÍTULO";
    public final String CERTIFICATE_TEXT_PARAGRAPH = "Certificamos (não oficialmente) que %s concluiu o curso POKE-MATICA GINASIO DAS FUNCOES com sucesso no dia %d às %h";
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

    // GAME
    public void startGame() {
        print("\n" + INTRODUCTION);
        handleOptions(OPTIONS_INIT);
    }

    // Handle the introduction, telling the game story and get user name
    public void handleIntroduction() {
        if (userName == NO_NAME) {
            System.out.print(ASK_NAME);
            userName = input.nextLine();
        }
        String inlineStory = STORY_01 + "\n" + STORY_02 + "\n" + STORY_03 + "\n" +
                STORY_04 + "\n" + STORY_05;
        print(inlineStory.replaceAll("%s", userName));
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

        System.out.println("HANDLE PLAY");
        handleOptions(OPTIONS_PLAY);
    }

    public void handleBattle() {
        int optionGym;
        System.out.println("selecione o ginasio a qual quer enfrentra");
        for (int i = 0; i < GYM_NAME.length; i++) {
            System.out.println(GYM_NAME[i]);
        }
        optionGym = input.nextInt();
        input.nextLine();
        SelectGym(optionGym);
         
        handleOptions(OPTIONS_PLAY);
    }

    public void SelectGym(int optionGym){
        int optionDefault = 0;
        do {
            switch (optionGym) {
                case 1:
                    System.out.println(GYM_NAME[0]);
                    break;
                case 2:
                    System.out.println(GYM_NAME[1]);
                    break;
                case 3:
                    System.out.println(GYM_NAME[2]);
                    break;
                case 4:
                    System.out.println(GYM_NAME[3]);
                    break;
                case 5:
                    System.out.println("função ginasio MTI");
                    break;
            
                default: System.out.println("opção invalida selecione novamente ");
                            optionDefault = 1;
                    break;
            }
        } while (optionDefault == 1);
    }

    public void handleTrain() {
        System.out.println("handle train");
        handleOptions(OPTIONS_PLAY);
    }

    public void handleBag() {
        System.out.println("handle bag");
        handleOptions(OPTIONS_PLAY);
    }

    public void handleSave() {
        System.out.println("handle save");
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
            System.out.println(ERROR_DEFAULT_MESSAGE);
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
    public void handleOptions(Map<Integer, String> options) {
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
    // PDF HANDLERS
    public void handleCertificateCreator() {
        try {
            openDocument();

            certificateVariablesInit();
            fillCertificateValues();

            closeDocument();

            print(
                    "Certificado Emitido com sucesso!\n" + CERTIFICATE_ABSOLUTE_PATH + CERTIFICATE_NAME + "\n");
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
        main.startGame();
    }
}

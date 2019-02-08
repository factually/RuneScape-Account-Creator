package utils;

/**
 * Created by Nowster on 17/12/2018.
 */

import data.AccountCreationResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class InternetHandler {


    private boolean useProxies = false;

    private void test() {
//        Option.builder()
    }

    private String requestURL(String urlString, String userAgent) throws IOException {
        HttpClient client = getClient();
        HttpGet get = new HttpGet(urlString);
        get.setHeader("User-Agent", userAgent);
        get.setHeader("Connection", "Keep-Alive");
        get.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "en-US");
        return getDecodedResponse(client.execute(get));
    }

    private String postURL(String urlString, List<NameValuePair> params, String userAgent) throws IOException {
        HttpClient client = getClient();
        HttpPost post = new HttpPost(urlString);
        post.setHeader("User-Agent", userAgent);
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("Referer", urlString);
        post.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "en-US");
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        Header[] headers = client.execute(post).getAllHeaders();

        for (Header header : headers) {
            System.out.println("Key = " + header.getName() + ", Value = " + header.getValue());
        }


        return getDecodedResponse(client.execute(post));
    }

    private String postAccountCreationURL(List<NameValuePair> params, String userAgent) throws IOException {
        return postURL("https://secure.runescape.com/m=account-creation/g=oldscape/create_account", params, userAgent);
    }

    public String getBalance(String apiKey, String userAgent) throws IOException {
        HttpClient client = getClient();
        String urlString = "http://2captcha.com/res.php?key=" + apiKey + "&action=getbalance";
        HttpPost post = new HttpPost(urlString);
        post.setHeader("User-Agent", userAgent);
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("Referer", urlString);
        post.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "en-US");
        return getDecodedResponse(client.execute(post));
    }

    public String getIP(String userAgent) throws IOException {
        HttpClient client = getClient();
        String urlString = "http://bot.whatismyipaddress.com/";
        HttpPost post = new HttpPost(urlString);

        post.setHeader("User-Agent", userAgent);
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("Referer", urlString);
        post.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "en-US");

        return getDecodedResponse(client.execute(post));
    }

    private HttpClient getClient() {
        return HttpClients.custom().useSystemProperties().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }

    private String requestSolveCaptcha(String apiKey, String userAgent) throws IOException {
        String requestUrl = requestURL("http://2captcha.com/in.php?key=" + apiKey + "&method=" + "userrecaptcha"
                + "&googlekey=" + "6LccFA0TAAAAAHEwUJx_c1TfTBWMTAOIphwTtd1b"
                + "&pageurl=" + "https://secure.runescape.com/m=account-creation/g=oldscape/create_account"
                + "&invisible=" + 1, userAgent);
        System.out.println("Resolving request\"" + requestUrl + "\"");
        return requestUrl;

        //return requestURL("http://2captcha.com/in.php?key=" + apiKey + "&method=" + "userrecaptcha"
        //        + "&googlekey=" + "6LccFA0TAAAAAHEwUJx_c1TfTBWMTAOIphwTtd1b"
        //        + "&pageurl=" + "https://secure.runescape.com/m=account-creation/g=oldscape/create_account"
        //        + "&invisible=" + "1", userAgent);
    }

    private String requestCaptchaResult(String apiKey, String captchaID, String userAgent) throws IOException {
        return requestURL("http://2captcha.com/res.php?key=" + apiKey + "&action=get&id=" + captchaID, userAgent);
    }

    private boolean captchaSolved(String result) {
        return result.substring(0, 2).equals("OK");
    }

    private String getCaptchaResult(String result) {
        return result.substring(3);
    }

    private String getDecodedResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        Header contentEncoding = entity.getContentEncoding();
        BufferedReader input = new BufferedReader(new InputStreamReader(getDecodedStream(entity.getContent(), (contentEncoding != null) ? entity.getContentEncoding().getValue() : null), "UTF-8"));
        StringBuilder res = new StringBuilder();
        String ln;
        while ((ln = input.readLine()) != null) {
            res.append(ln);
        }
        return res.toString();
    }

    private InputStream getDecodedStream(InputStream input, String encoding) throws IOException {
        if (encoding != null) {
            switch (encoding) {
                case "gzip": {
                    return new GZIPInputStream(input);
                }
                case "deflate": {
                    return new InflaterInputStream(input);
                }
                default:
                    break;
            }
        }
        return input;
    }

    private String makeCaptchaRequest(String apiKey, String userAgent) {
        boolean captchaRequested = false;
        int captchaAttempts = 0;
        String response = "";
        while (!captchaRequested) {
            if (captchaAttempts > 10) {
                captchaRequested = true;
            }
            try {
                response = requestSolveCaptcha(apiKey, userAgent);
                captchaRequested = true;
            } catch (IOException e) {
                try {
                    Thread.sleep(10000, 20000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            captchaAttempts++;
        }
        return response;
    }

    public AccountCreationResponse createAccount(String email, String password, String date, String apiKey, String userAgent, Random random) {

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("email1", email));
        params.add(new BasicNameValuePair("password1", password));

        String dateParsed[] = date.split(":");
        System.out.println("day=" + dateParsed[0]);
        System.out.println("month=" + dateParsed[1]);
        System.out.println("year=" + dateParsed[2]);

        params.add(new BasicNameValuePair("day", dateParsed[0]));
        params.add(new BasicNameValuePair("month", dateParsed[1]));
        params.add(new BasicNameValuePair("year", dateParsed[2]));
//        params.add(new BasicNameValuePair("submit", "Join Now"));
//        params.add(new BasicNameValuePair("onlyOneEmail", Integer.toString(1)));
        params.add(new BasicNameValuePair("create-submit", "Play Now"));

        String captchaRequest = makeCaptchaRequest(apiKey, userAgent);
        if (captchaRequest.equals("")) {
            return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "Couldn't get captcha request, trying again");
        }
        if (!captchaSolved(captchaRequest)) {
            return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "Couldn't get captcha request, trying again");
        }
        String captchaID = getCaptchaResult(captchaRequest);
        String googleCode = "";
        int requestResultTries = 0;
        while (googleCode.equals("") || googleCode.equals("CAPCHA_NOT_READY")) {
            if (requestResultTries >= 30) {
                return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "Couldn't get solved captcha, trying again");
            }

            try {
                Thread.sleep(9000L);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            try {
                googleCode = requestCaptchaResult(apiKey, captchaID, userAgent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ++requestResultTries;
        }
        if (!captchaSolved(googleCode)) {
            return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "Couldn't get solved captcha, trying again");
        }
        long responseTime = System.currentTimeMillis();
        long captchaExpiration = 150000 + random.nextInt(15000);
        params.add(new BasicNameValuePair("g-recaptcha-response", getCaptchaResult(googleCode)));

        String createResponse = "";
        while (createResponse.equals("") && System.currentTimeMillis() - responseTime < captchaExpiration) {
            try {

                try {
                    System.out.println("Waiting for captcha");
                    Thread.sleep(random.nextInt(5000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                createResponse = postAccountCreationURL(params, userAgent);
            } catch (IOException e) {
                return new AccountCreationResponse(AccountCreationResponse.Action.cProxy, "IP Blocked?");
            }
        }
        System.out.println("RESPONSE = " + createResponse);
        if (createResponse.equals("")) {
            return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "No response from account creation page, trying again");
        } else if (createResponse.contains("If your confirmation email has not arrived please check your spam filter.")) {
            return new AccountCreationResponse(AccountCreationResponse.Action.SAVE_ACCOUNT, "Account " + email + " successfully created");
        } else if (createResponse.contains("Your email address has already been taken.")) {
            return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "Email taken, trying again");
        } else if (createResponse.contains("Sorry, that character name is not available.")) {
            return new AccountCreationResponse(AccountCreationResponse.Action.TRY_AGAIN, "Display name taken, trying again");
        } else if (!useProxies) {
            return new AccountCreationResponse(AccountCreationResponse.Action.cProxy, "not registered1");
        }
        return new AccountCreationResponse(AccountCreationResponse.Action.cProxy, "not registered2");
    }
}

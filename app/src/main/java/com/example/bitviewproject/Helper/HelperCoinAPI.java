package com.example.bitviewproject.Helper;

import android.content.Context;
import android.os.Handler;

import com.example.bitviewproject.Model.CryptoCurrency;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Case;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HelperCoinAPI {

    private Realm realm;
    private final Handler handler;
    private String JSON_LIST_CURRENCIES;
    private static int globalCounter;
    private static OkHttpClient client = new OkHttpClient();

    public HelperCoinAPI(Context context, int globalCounter) {
        realm = Realm.getDefaultInstance();
        handler = new Handler(context.getMainLooper());
        HelperCoinAPI.globalCounter = globalCounter;
    }

    private void changeAPIUrl() {
        switch (globalCounter) {
            case 1:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/btc-eur";
                break;
            case 2:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/eth-eur";
                break;
            case 3:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/xrp-eur";
                break;
            case 4:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/bch-eur";
                break;
            case 5:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/eos-eur";
                break;
            case 6:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/ltc-eur";
                break;
            case 7:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/bnb-eur";
                break;
            case 8:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/bsv-eur";
                break;
            case 9:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/zec-eur";
                break;
            case 10:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/trx-eur";
                break;
            case 11:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/ada-eur";
                break;
            case 12:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/xmr-eur";
                break;
            case 13:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/dash-eur";
                break;
            case 14:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/miota-eur";
                break;
            case 15:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/etc-eur";
                break;
            case 16:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/neo-eur";
                break;
            case 17:
                JSON_LIST_CURRENCIES = "https://api.cryptonator.com/api/full/xem-eur";
                break;
        }
    }

    public void load() {
        while (globalCounter < 18) {
            changeAPIUrl();
            Request request = new Request.Builder()
                    .url(JSON_LIST_CURRENCIES)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // TODO: Fallo api
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String body = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseBpiResponse(body);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            globalCounter++;
        }
    }

    public void loadUpdate() {
        while (globalCounter < 18) {
            changeAPIUrl();
            Request request = new Request.Builder()
                    .url(JSON_LIST_CURRENCIES)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // TODO: Fallo api
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String body = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseBpiResponseUpdate(body);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            globalCounter++;
        }
    }

    public void parseBpiResponse(String body) throws JSONException {
        StringBuilder base = new StringBuilder();
        StringBuilder price = new StringBuilder();
        StringBuilder update = new StringBuilder();
        JSONObject jsonObject = new JSONObject(body);
        JSONObject tickerObject = jsonObject.getJSONObject("ticker");
        base.append(tickerObject.getString("base"));
        price.append(tickerObject.getString("price"));
        update.append(tickerObject.getString("change"));
        String name = base.toString();
        double value = Double.parseDouble(price.toString());
        double updateValue = Double.parseDouble(update.toString());
        setCurrency(name, value, updateValue);
    }

    public void parseBpiResponseUpdate(String body) throws JSONException {
        StringBuilder base = new StringBuilder();
        StringBuilder price = new StringBuilder();
        StringBuilder update = new StringBuilder();
        JSONObject jsonObject = new JSONObject(body);
        JSONObject tickerObject = jsonObject.getJSONObject("ticker");
        base.append(tickerObject.getString("base"));
        price.append(tickerObject.getString("price"));
        update.append(tickerObject.getString("change"));
        String name = base.toString();
        double value = Double.parseDouble(price.toString());
        double updateValue = Double.parseDouble(update.toString());
        updateCurrency(name, value, updateValue);
    }

    private synchronized void setCurrency(final String name, final double value, final double update) {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    CryptoCurrency first = bgRealm.where(CryptoCurrency.class).like("shortName", "*MONEDA", Case.INSENSITIVE).findFirst();
                    first.setShortName(name);
                    first.setValue(value);
                    first.setUpdate(update);
                    first.setFullName(setFullNameCurrency(name));
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // Transaction was a success.
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // Transaction failed and was automatically canceled.
                }
            });
        } finally {
            realm.close();
        }
    }

    private synchronized void updateCurrency(final String name, final double value, final double update) {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    CryptoCurrency first = bgRealm.where(CryptoCurrency.class).equalTo("shortName", name, Case.INSENSITIVE).findFirst();
                    first.setValue(value);
                    first.setUpdate(update);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // TODO: Transaction was a success.
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // TODO: Transaction failed and was automatically canceled.
                }
            });
        } finally {
            realm.close();
        }
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    private String setFullNameCurrency(String shortName){
            switch (shortName){
                case "BTC":
                    return "BitCoin";
                case "XRP":
                    return "Ripple";
                case "BCH":
                    return "Bitcoin Cash";
                case "EOS":
                    return "EOS";
                case "LTC":
                    return "Litecoin";
                case "BNB":
                    return "BNB";
                case "BSV":
                    return "Bitcoin SV";
                case "ZEC":
                    return "Zcash";
                case "TRX":
                    return "TRON";
                case "ADA":
                    return "Cardano";
                case "XMR":
                    return "Monero";
                case "DASH":
                    return "Dash";
                case "MIOTA":
                    return "MIOTA";
                case "ETC":
                    return "Ethereum Classic";
                case "NEO":
                    return "NEO";
                case "XEM":
                    return "NEM";
                case "ETH":
                    return "Ethereum";
            }
        return null;
    }

}

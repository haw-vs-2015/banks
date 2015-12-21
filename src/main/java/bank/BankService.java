package bank;

import bank.datatypes.*;
import bank.utils.CommandAdapter;
import bank.utils.HttpUtils;
import bank.utils.LamportClock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import mfc.util.IPManager;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

public class BankService {

    private final Map<String, HashMap<String, Account>> accounts = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, HashMap<String, Transfer>> transfers = Collections.synchronizedMap(new HashMap<>());
    private final SortedMap<Long, BankAction> actions = Collections.synchronizedSortedMap(new TreeMap<>());
    private LamportClock clock = new LamportClock();
    private Gson gson;

    public BankService() {
        Main.BANK_LIST.remove("http://localhost:" + Main.PORT);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Command.class, new CommandAdapter());
        gson = builder.create();

        post("/banks/:gameid/players", (req, res) -> {
            clock.tick();

            String gameId = req.params(":gameid");
            Account account;

            try {
                account = new Gson().fromJson(req.body(), Account.class);
            } catch (JsonSyntaxException e) {
                res.status(400);

                return "Malformed request";
            }

            if (gameId.isEmpty() || !account.isValid()) {
                res.status(400);

                return "Malformed request";
            }

            BankAction action = new BankAction(clock.getTime(), UUID.randomUUID().toString(), gameId, new CreateAccountCommand(account, gameId));
            Boolean result = null;
            boolean fail = false;

            actions.put(action.getLamportTimestamp(), action);

            for (String bankAddress : Main.BANK_LIST) {
                HttpUtils.HttpResult response = null;

                try {
                    response = HttpUtils.post(new URL(bankAddress + "/banks/actions"), gson.toJson(action, BankAction.class));
                } catch (IOException e) {
                    fail = true;
                }

                if (response == null || response.getStatusCode() != 200) {
                    fail = true;
                }
            }

            if (fail) {
                for (String bankAddress : Main.BANK_LIST) {
                    try {
                        HttpUtils.post(new URL(bankAddress + "/banks/actions/abort/" + action.getActionId()), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                res.status(503);

                return "Failure - Cannot execute action";
            } else {
                for (String bankAddress : Main.BANK_LIST) {
                    HttpUtils.HttpResult response = null;

                    try {
                        while (response == null || response.getStatusCode() != 200) {
                            response = HttpUtils.post(new URL(bankAddress + "/banks/actions/commit/" + action.getActionId()), "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            action.setConfirmed(true);

            synchronized (actions) {
                Iterator<Map.Entry<Long, BankAction>> iter = actions.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Long, BankAction> a = iter.next();

                    if (a.getValue().isConfirmed()) {
                        if (a.getValue().equals(action)) {
                            result = (Boolean) a.getValue().getCommand().exec(this);

                            iter.remove();

                            break;
                        } else {
                            a.getValue().getCommand().exec(this);

                            iter.remove();
                        }
                    } else {
                        break;
                    }
                }
            }

            if (result) {
                res.status(201);
                clock.tick();

                return "Bank account has been created";
            } else {
                res.status(409);
                clock.tick();

                return "Player already got a bank account";
            }
        });

        get("/banks/:gameid/players/:playerid", (req, res) -> {
            clock.tick();

            String gameId = req.params(":gameid");
            String playerId = req.params(":playerid");

            if (accounts.containsKey(gameId) && accounts.get(gameId).containsKey(playerId)) {
                res.status(200);
                clock.tick();

                return accounts.get(gameId).get(playerId).getSaldo();
            }

            res.status(404);
            clock.tick();

            return "Not found.";
        });

        post("/banks/:gameid/transfer/to/:to/:amount", (req, res) -> {
            clock.tick();

            String gameId = req.params(":gameid");
            String playerId = req.params(":to");
            int amount = 0;
            String reason = req.body();

            try {
                amount = Integer.parseInt(req.params(":amount"));
            } catch (NumberFormatException e) {
                res.status(400);

                return "Malformed request";
            }

            if (gameId.isEmpty() || playerId.isEmpty() || reason.isEmpty()) {
                res.status(400);

                return "Malformed request";
            }

            if (!accountExists(gameId, playerId)) {
                res.status(404);
                clock.tick();

                return "Not found.";
            }

            String actionId = UUID.randomUUID().toString();
            BankAction action = new BankAction(clock.getTime(), actionId, gameId, new TransferToCommand(playerId, amount, reason, gameId, actionId));
            ArrayList<Event> events = null;
            boolean fail = false;

            actions.put(action.getLamportTimestamp(), action);

            for (String bankAddress : Main.BANK_LIST) {
                HttpUtils.HttpResult response = null;

                try {
                    response = HttpUtils.post(new URL(bankAddress + "/banks/actions"), gson.toJson(action, BankAction.class));
                } catch (IOException e) {
                    fail = true;
                }

                if (response == null || response.getStatusCode() != 200) {
                    fail = true;
                }
            }

            if (fail) {
                for (String bankAddress : Main.BANK_LIST) {
                    try {
                        HttpUtils.post(new URL(bankAddress + "/banks/actions/abort/" + action.getActionId()), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                res.status(503);

                return "Failure - Cannot execute action";
            } else {
                for (String bankAddress : Main.BANK_LIST) {
                    HttpUtils.HttpResult response = null;

                    try {
                        while (response == null || response.getStatusCode() != 200) {
                            response = HttpUtils.post(new URL(bankAddress + "/banks/actions/commit/" + action.getActionId()), "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            action.setConfirmed(true);

            synchronized (actions) {
                Iterator<Map.Entry<Long, BankAction>> iter = actions.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Long, BankAction> a = iter.next();

                    if (a.getValue().isConfirmed()) {
                        if (a.getValue().equals(action)) {
                            events = (ArrayList<Event>) a.getValue().getCommand().exec(this);

                            iter.remove();

                            break;
                        } else {
                            a.getValue().getCommand().exec(this);

                            iter.remove();
                        }
                    } else {
                        break;
                    }
                }
            }


            res.status(201);
            clock.tick();

            return new Gson().toJson(events);
        });

        post("/banks/:gameid/transfer/from/:from/:amount", (req, res) -> {
            clock.tick();

            String gameId = req.params(":gameid");
            String playerId = req.params(":from");
            int amount = 0;
            String reason = req.body();

            try {
                amount = Integer.parseInt(req.params(":amount"));
            } catch (NumberFormatException e) {
                res.status(400);

                return "Malformed request";
            }

            if (gameId.isEmpty() || playerId.isEmpty() || reason.isEmpty()) {
                res.status(400);

                return "Malformed request";
            }

            if (!hasEnoughFonds(gameId, playerId, amount)) {
                res.status(403);
                clock.tick();

                return "Insufficient fonds";
            }

            if (!accountExists(gameId, playerId)) {
                res.status(404);
                clock.tick();

                return "Not found.";
            }

            String actionId = UUID.randomUUID().toString();
            BankAction action = new BankAction(clock.getTime(), actionId, gameId, new TransferFromCommand(playerId, amount, reason, gameId, actionId));
            ArrayList<Event> events = null;
            boolean fail = false;

            actions.put(action.getLamportTimestamp(), action);

            for (String bankAddress : Main.BANK_LIST) {
                HttpUtils.HttpResult response = null;

                try {
                    response = HttpUtils.post(new URL(bankAddress + "/banks/actions"), gson.toJson(action, BankAction.class));
                } catch (IOException e) {
                    fail = true;
                }

                if (response == null || response.getStatusCode() != 200) {
                    fail = true;
                }
            }

            if (fail) {
                for (String bankAddress : Main.BANK_LIST) {
                    try {
                        HttpUtils.post(new URL(bankAddress + "/banks/actions/abort/" + action.getActionId()), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                res.status(503);

                return "Failure - Cannot execute action";
            } else {
                for (String bankAddress : Main.BANK_LIST) {
                    HttpUtils.HttpResult response = null;

                    try {
                        while (response == null || response.getStatusCode() != 200) {
                            response = HttpUtils.post(new URL(bankAddress + "/banks/actions/commit/" + action.getActionId()), "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            action.setConfirmed(true);

            synchronized (actions) {
                Iterator<Map.Entry<Long, BankAction>> iter = actions.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Long, BankAction> a = iter.next();

                    if (a.getValue().isConfirmed()) {
                        if (a.getValue().equals(action)) {
                            events = (ArrayList<Event>) a.getValue().getCommand().exec(this);

                            iter.remove();

                            break;
                        } else {
                            a.getValue().getCommand().exec(this);

                            iter.remove();
                        }
                    } else {
                        break;
                    }
                }
            }

            res.status(201);
            clock.tick();

            return new Gson().toJson(events);
        });

        post("/banks/:gameid/transfer/from/:from/to/:to/:amount", (req, res) -> {
            clock.tick();

            String gameId = req.params(":gameid");
            String playerAId = req.params(":from");
            String playerBId = req.params(":to");
            int amount = 0;
            String reason = req.body();

            try {
                amount = Integer.parseInt(req.params(":amount"));
            } catch (NumberFormatException e) {
                res.status(400);

                return "Malformed request";
            }

            if (gameId.isEmpty() || playerAId.isEmpty() || playerBId.isEmpty() || reason.isEmpty()) {
                res.status(400);

                return "Malformed request";
            }

            if (!hasEnoughFonds(gameId, playerAId, amount)) {
                res.status(403);
                clock.tick();

                return "Insufficient fonds";
            }

            if (!accountExists(gameId, playerAId) || !accountExists(gameId, playerBId)) {
                res.status(404);
                clock.tick();

                return "Not found.";
            }

            String actionId = UUID.randomUUID().toString();
            BankAction action = new BankAction(clock.getTime(), actionId, gameId, new TransferFromToCommand(playerAId, playerBId, amount, reason, gameId, actionId));
            ArrayList<Event> events = null;
            boolean fail = false;

            actions.put(action.getLamportTimestamp(), action);

            for (String bankAddress : Main.BANK_LIST) {
                HttpUtils.HttpResult response = null;

                try {
                    response = HttpUtils.post(new URL(bankAddress + "/banks/actions"), gson.toJson(action, BankAction.class));
                } catch (IOException e) {
                    fail = true;
                }

                if (response == null || response.getStatusCode() != 200) {
                    fail = true;
                }
            }

            if (fail) {
                for (String bankAddress : Main.BANK_LIST) {
                    try {
                        HttpUtils.post(new URL(bankAddress + "/banks/actions/abort/" + action.getActionId()), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                res.status(503);

                return "Failure - Cannot execute action";
            } else {
                for (String bankAddress : Main.BANK_LIST) {
                    HttpUtils.HttpResult response = null;

                    try {
                        while (response == null || response.getStatusCode() != 200) {
                            response = HttpUtils.post(new URL(bankAddress + "/banks/actions/commit/" + action.getActionId()), "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            action.setConfirmed(true);

            synchronized (actions) {
                Iterator<Map.Entry<Long, BankAction>> iter = actions.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Long, BankAction> a = iter.next();

                    if (a.getValue().isConfirmed()) {
                        if (a.getValue().equals(action)) {
                            events = (ArrayList<Event>) a.getValue().getCommand().exec(this);

                            iter.remove();

                            break;
                        } else {
                            a.getValue().getCommand().exec(this);

                            iter.remove();
                        }
                    } else {
                        break;
                    }
                }
            }

            res.status(201);
            clock.tick();

            return new Gson().toJson(events);
        });

        get("/banks/:gameid/transfers/:transferid", (req, res) -> {
            clock.tick();

            String gameId = req.params(":gameid");
            String transferId = req.params(":transferid");

            if (gameId.isEmpty() || transferId.isEmpty()) {
                res.status(400);
                clock.tick();

                return "Malformed request";
            }

            if (!transfers.containsKey(gameId) || !transfers.get(gameId).containsKey(transferId)) {
                res.status(404);
                clock.tick();

                return "Not found.";
            }

            res.status(200);
            clock.tick();

            return new Gson().toJson(transfers.get(gameId).get(transferId));
        });

        post("/banks/actions", (req, res) -> {
            BankAction action;

            try {
                action = gson.fromJson(req.body(), BankAction.class);
            } catch (JsonSyntaxException e) {
                res.status(400);

                return "Malformed request";
            }

            synchronized (actions) {
                if (!actions.values().contains(action)) {
                    actions.put(action.getLamportTimestamp(), action);
                    clock.adjustTime(action.getLamportTimestamp());
                }
            }

            res.status(200);

            return "Received";
        });

        post("banks/actions/commit/:actionid", (req, res) -> {
            String actionId = req.params(":actionId");

            if (actionId.isEmpty()) {
                res.status(400);

                return "Malformed request";
            }

            synchronized (actions) {
                Iterator<Map.Entry<Long, BankAction>> iter = actions.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Long, BankAction> a = iter.next();

                    if (a.getValue().getActionId().equals(actionId)) {
                        a.getValue().setConfirmed(true);
                    }

                    if (a.getValue().isConfirmed()) {
                        if (!transfers.containsKey(a.getValue().getGameId()) || !transfers.get(a.getValue().getGameId()).containsKey(a.getValue().getActionId()) && !transfers.get(a.getValue().getGameId()).containsKey(new StringBuilder(a.getValue().getActionId()).reverse().toString())) {
                            if (a.getValue().getActionId().equals(actionId)) {
                                a.getValue().getCommand().exec(this);
                                iter.remove();

                                break;
                            } else {
                                a.getValue().getCommand().exec(this);

                                iter.remove();
                            }
                        } else {
                            iter.remove();
                        }
                    } else {
                        res.status(409);

                        return "Cannot execute yet";
                    }
                }
            }

            res.status(200);

            return "Executed";
        });

        post("/banks/actions/abort/:actionid", (req, res) -> {
            String actionId = req.params(":actionId");

            if (actionId.isEmpty()) {
                res.status(400);

                return "Malformed request";
            }

            synchronized (actions) {
                Iterator<Map.Entry<Long, BankAction>> iter = actions.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Long, BankAction> a = iter.next();

                    if (a.getValue().getActionId().equals(actionId)) {
                        iter.remove();
                    }
                }
            }

            res.status(200);

            return "Deleted";
        });

    }

    public boolean createAccount(Account account, String gameId) {
        clock.tick();

        synchronized (accounts) {
            if (!accounts.containsKey(gameId)) {
                accounts.put(gameId, new HashMap<>());
                accounts.get(gameId).put(account.getPlayer().getId(), account);
                transfers.put(gameId, new HashMap<>());

                return true;
            } else if (!accounts.get(gameId).containsKey(account.getPlayer().getId())) {
                accounts.get(gameId).put(account.getPlayer().getId(), account);

                return true;
            }
        }

        return false;
    }

    public ArrayList<Event> transferTo(String playerId, int amount, String reason, String gameId, String actionId) throws IOException {
        clock.tick();

        if (accounts.containsKey(gameId) && accounts.get(gameId).containsKey(playerId)) {
            Account account = accounts.get(gameId).get(playerId);

            account.beginTransaction();
                account.addSaldo(amount);
            account.commit();

            ArrayList<Event> events = new ArrayList<>();
            Event event = new Event();
            Transfer transfer = new Transfer();

            event.setType("Deposit");
            event.setName("Transfers the given amount of money from the bank itself to the given account.");
            event.setReason(reason);
            event.setPlayer(account.getPlayer());
            event.setResource("http://" + IPManager.getInternetIP() + ":4567/banks/" + gameId + "/transfers/" + actionId);

            //HttpUtils.HttpResult result = HttpUtils.post(new URL("http://0.0.0.0:4567/events?gameid=" + gameId), new Gson().toJson(event));

            transfer.setTo(playerId);
            transfer.setFrom("bank");
            transfer.setAmount(amount);
            transfer.setReason(reason);
            //transfer.setEvent(result.body);

            this.transfers.get(gameId).put(actionId, transfer);
            events.add(event);

            return events;
        }

        return new ArrayList<>();
    }

    public ArrayList<Event> transferFrom(String playerId, int amount, String reason, String gameId, String actionId) throws IOException {
        clock.tick();

        if (accounts.containsKey(gameId) && accounts.get(gameId).containsKey(playerId)) {
            Account account = accounts.get(gameId).get(playerId);

            try {
                account.beginTransaction();
                    account.subSaldo(amount);
                account.commit();
            } catch (Exception e) {
                account.rollback();
                account.commit();

                return null;
            }

            ArrayList<Event> events = new ArrayList<>();
            Event event = new Event();
            Transfer transfer = new Transfer();

            event.setType("Disbursal");
            event.setName("Transfers the given amount of money from the given account to the bank itself.");
            event.setReason(reason);
            event.setPlayer(account.getPlayer());
            event.setResource("http://" + IPManager.getInternetIP() + ":4567/banks/" + gameId + "/transfers/" + actionId);

            //HttpUtils.HttpResult result = HttpUtils.post(new URL("http://0.0.0.0:4567/events?gameid=" + gameId), new Gson().toJson(event));

            transfer.setTo(playerId);
            transfer.setFrom("bank");
            transfer.setAmount(amount);
            transfer.setReason(reason);
            //transfer.setEvent(result.getBody());

            this.transfers.get(gameId).put(actionId, transfer);
            events.add(event);

            return events;
        }

        return new ArrayList<>();
    }

    public ArrayList<Event> transferFromTo(String playerAId, String playerBId, int amount, String reason, String gameId, String actionAId, String actionBId) throws IOException {
        clock.tick();

        if (accounts.containsKey(gameId) && accounts.get(gameId).containsKey(playerAId) && accounts.get(gameId).containsKey(playerBId)) {
            Account accountA = accounts.get(gameId).get(playerAId);
            Account accountB = accounts.get(gameId).get(playerBId);

            try {
                accountA.beginTransaction();
                accountB.beginTransaction();
                    accountA.subSaldo(amount);
                    accountB.addSaldo(amount);
                accountA.commit();
                accountB.commit();
            } catch (Exception e) {
                accountA.rollback();
                accountB.rollback();
                accountA.commit();
                accountB.commit();

                return null;
            }

            ArrayList<Event> events = new ArrayList<>();
            Event eventA = new Event();
            Event eventB = new Event();
            Transfer transferA = new Transfer();
            Transfer transferB = new Transfer();

            eventA.setType("Disbursal");
            eventA.setName("Transfers the given amount of money from the given account to another account.");
            eventA.setReason(reason);
            eventA.setPlayer(accountA.getPlayer());
            eventA.setResource("http://" + IPManager.getInternetIP() + ":4567/banks/" + gameId + "/transfers/" + actionAId);

            //HttpUtils.HttpResult result = HttpUtils.post(new URL("http://0.0.0.0:4567/events?gameid=" + gameId), new Gson().toJson(eventA));

            transferA.setTo(playerBId);
            transferA.setFrom(playerAId);
            transferA.setAmount(amount);
            transferA.setReason(reason);
            //transferA.setEvent(result.getBody());

            eventB.setType("Deposit");
            eventB.setName("Transfers the given amount of money from another account to the given account.");
            eventB.setReason(reason);
            eventB.setPlayer(accountB.getPlayer());
            eventB.setResource("http://" + IPManager.getInternetIP() + ":4567/banks/" + gameId + "/transfers/" + actionBId);

            //result = HttpUtils.post(new URL("http://0.0.0.0:4567/events?gameid=" + gameId), new Gson().toJson(eventB));

            transferB.setTo(playerBId);
            transferB.setFrom(playerAId);
            transferB.setAmount(amount);
            transferB.setReason(reason);
            //transferB.setEvent(result.getBody());

            this.transfers.get(gameId).put(actionAId, transferA);
            this.transfers.get(gameId).put(actionBId, transferB);
            events.add(eventA);
            events.add(eventB);

            return events;
        }

        return new ArrayList<>();
    }

    private boolean hasEnoughFonds(String gameId, String playerId, int amount) {
        return accounts.get(gameId).get(playerId).getSaldo() >= amount;
    }

    private boolean accountExists(String gameId, String playerId) {
        return accounts.containsKey(gameId) && accounts.get(gameId).containsKey(playerId);
    }

}

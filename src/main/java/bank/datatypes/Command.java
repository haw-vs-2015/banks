package bank.datatypes;

import bank.BankService;

public interface Command {

    Object exec(BankService service) throws Exception;

}

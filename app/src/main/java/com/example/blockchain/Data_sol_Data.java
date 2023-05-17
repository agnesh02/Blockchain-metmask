package com.example.blockchain;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
public class Data_sol_Data extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610235806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80631c2921231461003b578063d81aa8c414610050575b600080fd5b60005460405190815260200160405180910390f35b61006361005e3660046100d1565b610065565b005b60016000808282546100779190610182565b909155505060405133907fb5c2ea85be353a1bfa0af8a9c0320de6ea9bf15532b2be14d635ffac22abe8fb906100b090429085906101a9565b60405180910390a250565b634e487b7160e01b600052604160045260246000fd5b6000602082840312156100e357600080fd5b813567ffffffffffffffff808211156100fb57600080fd5b818401915084601f83011261010f57600080fd5b813581811115610121576101216100bb565b604051601f8201601f19908116603f01168101908382118183101715610149576101496100bb565b8160405282815287602084870101111561016257600080fd5b826020860160208301376000928101602001929092525095945050505050565b808201808211156101a357634e487b7160e01b600052601160045260246000fd5b92915050565b82815260006020604081840152835180604085015260005b818110156101dd578581018301518582016060015282016101c1565b506000606082860101526060601f19601f83011685010192505050939250505056fea2646970667358221220f3413a14e3477e1fd26971111125ef1d6ebb65d6424c23c8e7a77dc68740467c64736f6c63430008110033";

    public static final String FUNC_ADDRECORD = "addRecord";

    public static final String FUNC_GETNUMBEROFRECORDS = "getnumberOfRecords";

    public static final Event NEWRECORD_EVENT = new Event("NewRecord", 
            Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));



    protected Data_sol_Data(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }



    protected Data_sol_Data(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<NewRecordEventResponse> getNewRecordEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWRECORD_EVENT, transactionReceipt);
        ArrayList<NewRecordEventResponse> responses = new ArrayList<>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewRecordEventResponse typedResponse = new NewRecordEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.message = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewRecordEventResponse> newRecordEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> {
            EventValuesWithLog eventValues = extractEventParametersWithLog(NEWRECORD_EVENT, log);
            NewRecordEventResponse typedResponse = new NewRecordEventResponse();
            typedResponse.log = log;
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.message = (String) eventValues.getNonIndexedValues().get(1).getValue();
            return typedResponse;
        });
    }

    public Flowable<NewRecordEventResponse> newRecordEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWRECORD_EVENT));
        return newRecordEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addRecord(String _message) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDRECORD,
                Collections.singletonList(new Utf8String(_message)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getnumberOfRecords() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETNUMBEROFRECORDS,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static Data_sol_Data load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Data_sol_Data(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Data_sol_Data load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Data_sol_Data(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Data_sol_Data> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Data_sol_Data.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static class NewRecordEventResponse extends BaseEventResponse {
        public String from;

        public BigInteger timestamp;

        public String message;
    }
}

package demo.Comsumer;

import com.demo.rpc.API.Calculator;
import com.demo.rpc.RPC.CalculateRpcRequest;
import com.demo.rpc.RPC.RPCObject;
import com.demo.rpc.Reflex.Reflex;

import java.util.ArrayList;
import java.util.List;

public class ComsumerApp {

    public static void main(String[] args) {
        Reflex reflex = new Reflex();
        reflex.doScanner("com.demo.rpc");
        reflex.doInstance();
        Object object = Reflex.test("CalculatorRemoteImpl");
        if(object instanceof Calculator) {

            //Calculator calculator = (Calculator)object;
            RPCObject rpc = new RPCObject();
            rpc.setMethod("add");
            List<Object> pars = new ArrayList<>();
            pars.add(1);
            pars.add(2);
            rpc.setMyClass("CalculatorRemoteImpl");
            rpc.setParamValues(pars);
            rpc.setReturnTypes(new Class[]{int.class,int.class});
            CalculateRpcRequest request = new CalculateRpcRequest();
            int response = request.getResponse(rpc);
            System.out.println("result:"+response);
        }
    }
}

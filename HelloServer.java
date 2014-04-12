import HelloApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Properties;

class HelloImpl extends HelloPOA{
  private ORB orb;

  public void setORB(ORB orb_val){
    orb = orb_val;
  }
  
  public String sayHello(){
    return "\nHello world !!\n";
  }
  
  public void shutdown(){
    orb.shutdown(false);
  }
}

public class HelloServer{

  public static void main(String args[]){
    try{
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // Get reference to rootpoa & activate the POAManager
      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();

      // create servant and register it with the ORB
      HelloImpl helloImpl = new HelloImpl();
      helloImpl.setORB(orb); 
      // create a tie, with servant being the delegate.
      HelloPOATie tie = new HelloPOATie(helloImpl, rootpoa);

      // obtain the objectRef for the tie
      // this step also implicitly activates the 
      // the object
      Hello href = tie._this(orb);
	    
      // get the root naming context
      org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
      
      // Use NamingContextExt which is part of the Interoperable
      // Naming Service specification.
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // bind the Object Reference in Naming
      String name = "Hello";
      NameComponent path[] = ncRef.to_name( name );
      ncRef.rebind(path, href);

      System.out.println("HelloServer ready and waiting ...");

      // wait for invocations from clients
      orb.run();
      } 
      
    catch (Exception e){
      System.err.println("ERROR: " + e);
      e.printStackTrace(System.out);
    }
    
    System.out.println("HelloServer Exiting ...");
	
  }
}

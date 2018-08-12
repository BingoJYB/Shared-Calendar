public class getTokenHandler {
	public boolean receiveToken(){		
		boolean isValidState = false;

		// Check if node is in valid state
        if (!nodeWithToken.accessing && !nodeWithToken.hasToken) {
        	nodeWithToken.hasToken = true;
            isValidState = true;
            
            if (!nodeWithToken.wantToAccess) {
                // Pass Token after some amount of time
                Thread tokenPassThread = new Thread() {
                	public void run() {
                		// Wait 200 ms before passing the token
                        try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

                        // Only pass token, if the node has the token and don't want to use it
                        if (nodeWithToken.hasToken && !nodeWithToken.wantToAccess)
                        {
                            if (!tokenOperation.passToken(node.memberInfo.toList()))
                            {
                                System.err.println("Couldn't pass token");
                            }
                        }
                    }
                };
                tokenPassThread.start();
            }
        }

        return isValidState;
	}
}
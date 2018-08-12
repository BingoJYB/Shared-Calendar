public class getAlgorithmHandler {
	public String getSyncMethod(){
		String algorithmType=null;
		
		if (synAlgorithmOperation.tokenRing==true)
			algorithmType=synAlgorithmOperation.tokenRingName;
		else if (synAlgorithmOperation.RandA==true)
			algorithmType=synAlgorithmOperation.ricartAgrawalaName;
		
		return algorithmType;
	}
}
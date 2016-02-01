package devicemanager.mobile.model;

import com.android.ddmlib.Client;

import devicemanager.model.BasicModel;

public class ClientModel extends BasicModel{

	Client client;
	public ClientModel(Client c)
	{
		client=c;
	}

	@Override
	public String getID() {
		return getName();
	}

	@Override
	public String getName() {
		//System.out.println(client.getClientData().getClientDescription());
		return client.getClientData().getClientDescription();
	}

}

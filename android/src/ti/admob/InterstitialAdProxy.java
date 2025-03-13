/**
 * Copyright (c) 2011 by Studio Classics. All Rights Reserved.
 * Copyright (c) 2017-present by Axway Appcelerator. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.admob;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

@Kroll.proxy(creatableInModule = AdmobModule.class)
public class InterstitialAdProxy extends KrollProxy
{

	private final String TAG = "InterstitialAd";
	private InterstitialAd mInterstitialAd;
	String adId = "";

	public InterstitialAdProxy()
	{
		//this.interstitialAd = new InterstitialAd(getActivity());
		//this.interstitialAd.setAdListener(new CommonAdListener(this, TAG));
	}

	@Override
	public void handleCreationDict(KrollDict dict)
	{
		super.handleCreationDict(dict);
		if (dict.containsKeyAndNotNull(AdmobModule.PROPERTY_AD_UNIT_ID)) {
			//this.interstitialAd.setAdUnitId(dict.getString(AdmobModule.PROPERTY_AD_UNIT_ID));
			adId = dict.getString(AdmobModule.PROPERTY_AD_UNIT_ID);
		}
	}

	// clang format off
	@Kroll.method
	@Kroll.setProperty
	public void setAdUnitId(String adUnitId)
	// clang format on
	{
		// Validate the parameter
		if (adUnitId != null && adUnitId instanceof String) {
			//
		}
	}

	// clang format off
	@Kroll.method
	@Kroll.getProperty
	public String getAdUnitId()
	// clang format on
	{
		return mInterstitialAd.getAdUnitId();
	}

	@Kroll.method
	public void load(final KrollDict options)
	{
		if (options != null && options.containsKeyAndNotNull("adUnitId")) {
			adId = options.getString("adUnitId");
		}

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		if (options != null && options.containsKeyAndNotNull("extras")) {
			Bundle extras = AdmobModule.mapToBundle(options.getKrollDict("extras"));
			adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
		}

		InterstitialAd.load(TiApplication.getInstance(), adId, adRequestBuilder.build(),
							new InterstitialAdLoadCallback() {
								@Override
								public void onAdLoaded(@NonNull InterstitialAd interstitialAd)
								{
									mInterstitialAd = interstitialAd;
									Log.d(TAG, "Interstitial ad loaded");
									fireEvent("load", new KrollDict());
								}

								@Override
								public void onAdFailedToLoad(@NonNull LoadAdError loadAdError)
								{
									Log.e(TAG, "Interstitial ad failed to load: " + loadAdError.getMessage());
									KrollDict error = new KrollDict();
									error.put("message", loadAdError.getMessage());
									error.put("code", loadAdError.getCode());
									fireEvent("fail", error);
								}
							});
	}

	@Kroll.method
	public void show()
	{
		if (mInterstitialAd != null) {
			mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
				@Override
				public void onAdDismissedFullScreenContent()
				{
					fireEvent("close", new KrollDict());
				}

				@Override
				public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError)
				{
					KrollDict error = new KrollDict();
					error.put("message", adError.getMessage());
					error.put("code", adError.getCode());
					fireEvent("fail", error);
				}

				@Override
				public void onAdShowedFullScreenContent()
				{
					fireEvent("open", new KrollDict());
				}
			});
			mInterstitialAd.show(TiApplication.getAppCurrentActivity());
		} else {
			Log.d(TAG, "The interstitial ad wasn't ready yet.");
		}
	}
}

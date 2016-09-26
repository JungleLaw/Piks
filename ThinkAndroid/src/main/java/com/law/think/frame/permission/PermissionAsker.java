package com.law.think.frame.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.SDKUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class PermissionAsker {
	public static final String TAG = PermissionAsker.class.getSimpleName();
	private static Activity mActivity;

	private static PermissionAsker instance;

	public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {

		void onPermissionsGranted(int requestCode, List<String> perms);

		void onPermissionsDenied(int requestCode, List<String> perms);

	}

	private PermissionAsker() {

	}

	public static PermissionAsker getInstance(Activity activity) {
		if (instance == null) {
			synchronized (PermissionAsker.class) {
				if (instance == null) {
					instance = new PermissionAsker();
				}
			}
		}
		PermissionAsker.mActivity = activity;
		return instance;
	}

	public static PermissionAsker getInstance(Fragment fragment) {
		PermissionAsker.mActivity = fragment.getActivity();
		return instance;
	}

	public static PermissionAsker getInstance(android.support.v4.app.Fragment fragmentV4) {
		PermissionAsker.mActivity = fragmentV4.getActivity();
		return instance;
	}

	public static PermissionAsker getInstance(FragmentActivity fragmentActivity) {
		PermissionAsker.mActivity = fragmentActivity;
		return instance;
	}

	public static PermissionAsker getInstance(AppCompatActivity appCompatActivity) {
		PermissionAsker.mActivity = appCompatActivity;
		return instance;
	}

	/**
	 * Check if the calling context has a set of permissions.
	 *
	 * @param context
	 *            the calling context.
	 * @param perms
	 *            one ore more permissions, such as
	 *            {@code android.Manifest.permission.CAMERA}.
	 * @return true if all permissions are already granted, false if at least
	 *         one permission is not yet granted.
	 */
	public boolean hasPermissions(String... perms) {
		// Always return true for SDK < M, let the system deal with the
		// permissions
		if (!SDKUtils.hasM()) {
			Logger.e(TAG, "hasPermissions: API version < M, returning true by default");
			return true;
		}

		for (String perm : perms) {
			boolean hasPerm = (ContextCompat.checkSelfPermission(mActivity, perm) == PackageManager.PERMISSION_GRANTED);
			if (!hasPerm) {
				return false;
			}
		}

		return true;
	}

	public boolean hasPermissions(List<String> perms) {
		return hasPermissions(perms.toArray(new String[] {}));
	}

	/**
	 * Request a set of permissions, showing rationale if the system requests
	 * it.
	 *
	 * @param object
	 *            Activity or Fragment requesting permissions. Should implement
	 *            {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
	 *            or
	 *            {@link android.support.v13.app.FragmentCompat.OnRequestPermissionsResultCallback}
	 * @param rationale
	 *            a message explaining why the application needs this set of
	 *            permissions, will be displayed if the user rejects the request
	 *            the first time.
	 * @param requestCode
	 *            request code to track this request, must be < 256.
	 * @param perms
	 *            a set of permissions to be requested.
	 */
	private void askPermissions(final Object object, String rationale, @StringRes int positiveButton,
			@StringRes int negativeButton, final int requestCode, final String... perms) {
		checkCallingObjectSuitability(object);

		boolean shouldShowRationale = false;
		for (String perm : perms) {
			shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
		}

		if (shouldShowRationale) {
			Activity activity = mActivity;
			if (null == activity) {
				return;
			}

			AlertDialog dialog = new AlertDialog.Builder(activity).setMessage(rationale)
					.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							executePermissionsRequest(object, perms, requestCode);
						}
					}).setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// act as if the permissions were denied
							if (object instanceof PermissionCallbacks) {
								((PermissionCallbacks) object).onPermissionsDenied(requestCode, Arrays.asList(perms));
							}
						}
					}).create();
			dialog.show();
		} else {
			executePermissionsRequest(object, perms, requestCode);
		}
	}

	public void askPermissions(final Object object, String rationale, final int requestCode, final List<String> perms) {
		askPermissions(object, rationale, android.R.string.ok, android.R.string.cancel, requestCode,
				perms.toArray(new String[] {}));
	}

	public void askPermissions(final Object object, String rationale, final int requestCode, final String... perms) {
		askPermissions(object, rationale, android.R.string.ok, android.R.string.cancel, requestCode, perms);
	}

	public static void onAskPermissionsResult(int requestCode, String[] permissions, int[] grantResults,
			Object object) {

		checkCallingObjectSuitability(object);

		// Make a collection of granted and denied permissions from the request.
		ArrayList<String> granted = new ArrayList<>();
		ArrayList<String> denied = new ArrayList<>();
		for (int i = 0; i < permissions.length; i++) {
			String perm = permissions[i];
			if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
				granted.add(perm);
			} else {
				denied.add(perm);
			}
		}

		// Report granted permissions, if any.
		if (!granted.isEmpty()) {
			// Notify callbacks
			if (object instanceof PermissionCallbacks) {
				((PermissionCallbacks) object).onPermissionsGranted(requestCode, granted);
			}
		}

		// Report denied permissions, if any.
		if (!denied.isEmpty()) {
			if (object instanceof PermissionCallbacks) {
				((PermissionCallbacks) object).onPermissionsDenied(requestCode, denied);
			}
		}

		// If 100% successful, call annotated methods
		// if (!granted.isEmpty() && denied.isEmpty()) {
		// runAnnotatedMethods(object, requestCode);
		// }
	}

	@TargetApi(23)
	private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
		if (object instanceof Activity) {
			return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
		} else if (object instanceof Fragment) {
			return ((android.support.v4.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
		} else if (object instanceof android.app.Fragment) {
			return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
		} else {
			return false;
		}
	}

	@TargetApi(23)
	private static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
		checkCallingObjectSuitability(object);
		if (object instanceof Activity) {
			ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
		} else if (object instanceof Fragment) {
			((Fragment) object).requestPermissions(perms, requestCode);
		} else if (object instanceof android.app.Fragment) {
			((android.app.Fragment) object).requestPermissions(perms, requestCode);
		}
	}

	// private static void runAnnotatedMethods(Object object, int requestCode) {
	// Class clazz = object.getClass();
	// if (isUsingAndroidAnnotations(object)) {
	// clazz = clazz.getSuperclass();
	// }
	// for (Method method : clazz.getDeclaredMethods()) {
	// if (method.isAnnotationPresent(AfterPermissionGranted.class)) {
	// // Check for annotated methods with matching request code.
	// AfterPermissionGranted ann =
	// method.getAnnotation(AfterPermissionGranted.class);
	// if (ann.value() == requestCode) {
	// // Method must be void so that we can invoke it
	// if (method.getParameterTypes().length > 0) {
	// throw new RuntimeException("Cannot execute non-void method " +
	// method.getName());
	// }
	//
	// try {
	// // Make method accessible if private
	// if (!method.isAccessible()) {
	// method.setAccessible(true);
	// }
	// method.invoke(object);
	// } catch (IllegalAccessException e) {
	// Log.e(TAG, "runDefaultMethod:IllegalAccessException", e);
	// } catch (InvocationTargetException e) {
	// Log.e(TAG, "runDefaultMethod:InvocationTargetException", e);
	// }
	// }
	// }
	// }
	// }

	private static void checkCallingObjectSuitability(Object object) {
		// Make sure Object is an Activity or Fragment
		boolean isActivity = object instanceof Activity;
		boolean isSupportFragment = object instanceof android.support.v4.app.Fragment;
		boolean isAppFragment = object instanceof Fragment;
		boolean isMinSdkM = SDKUtils.hasM();

		if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
			if (isAppFragment) {
				throw new IllegalArgumentException(
						"Target SDK needs to be greater than 23 if caller is android.app.Fragment");
			} else {
				throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
			}
		}
	}

	private static boolean isUsingAndroidAnnotations(Object object) {
		if (!object.getClass().getSimpleName().endsWith("_")) {
			return false;
		}

		try {
			Class clazz = Class.forName("org.androidannotations.api.view.HasViews");
			return clazz.isInstance(object);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}

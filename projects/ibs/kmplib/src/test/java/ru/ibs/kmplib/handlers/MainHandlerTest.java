package ru.ibs.kmplib.handlers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import ru.ibs.kmplib.bean.KmpMedicamentPrescribe;
import ru.ibs.kmplib.bean.RangeUpdateBean;
import ru.ibs.kmplib.bean.UpdateBean;

/**
 *
 * @author me
 */
public class MainHandlerTest {

	@Test
	public void test() {
		MainHandler mainHandler = new MainHandler(null, null, null);
		List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = Arrays.asList(createKmpMedicamentPrescribe(1L, "String1"), createKmpMedicamentPrescribe(2L, "String1"), createKmpMedicamentPrescribe(3L, "String2"), createKmpMedicamentPrescribe(4L, "String2"), createKmpMedicamentPrescribe(5L, "String2"), createKmpMedicamentPrescribe(6L, "String1"), createKmpMedicamentPrescribe(7L, "String2"));
		List<UpdateBean> updateBeanList = mainHandler.getUpdateBeanList(kmpMedicamentPrescribeList);
		Assert.assertTrue(updateBeanList.size() == 4);
		Assert.assertTrue(updateBeanList.get(0) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(0)).getId().equals(1L) && ((RangeUpdateBean) updateBeanList.get(0)).getId2().equals(2L));
		Assert.assertTrue(updateBeanList.get(1) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(1)).getId().equals(3L) && ((RangeUpdateBean) updateBeanList.get(1)).getId2().equals(5L));
		Assert.assertTrue(updateBeanList.get(2) instanceof UpdateBean && updateBeanList.get(2).getId().equals(6L));
		Assert.assertTrue(updateBeanList.get(3) instanceof UpdateBean && updateBeanList.get(3).getId().equals(7L));
	}

	@Test
	public void test2() {
		MainHandler mainHandler = new MainHandler(null, null, null);
		List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = Arrays.asList(createKmpMedicamentPrescribe(1L, "String1"), createKmpMedicamentPrescribe(2L, "String1"), createKmpMedicamentPrescribe(3L, "String2"), createKmpMedicamentPrescribe(4L, "String2"), createKmpMedicamentPrescribe(5L, "String2"), createKmpMedicamentPrescribe(6L, "String1"), createKmpMedicamentPrescribe(7L, "String2"), createKmpMedicamentPrescribe(8L, "String2"));
		List<UpdateBean> updateBeanList = mainHandler.getUpdateBeanList(kmpMedicamentPrescribeList);
		Assert.assertTrue(updateBeanList.size() == 4);
		Assert.assertTrue(updateBeanList.get(0) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(0)).getId().equals(1L) && ((RangeUpdateBean) updateBeanList.get(0)).getId2().equals(2L));
		Assert.assertTrue(updateBeanList.get(1) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(1)).getId().equals(3L) && ((RangeUpdateBean) updateBeanList.get(1)).getId2().equals(5L));
		Assert.assertTrue(updateBeanList.get(3) instanceof UpdateBean && updateBeanList.get(3).getId().equals(6L));
		Assert.assertTrue(updateBeanList.get(2) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(2)).getId().equals(7L) && ((RangeUpdateBean) updateBeanList.get(2)).getId2().equals(8L));
	}

	@Test
	public void test3() {
		MainHandler mainHandler = new MainHandler(null, null, null);
		List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = Arrays.asList(createKmpMedicamentPrescribe(1L, "String1"), createKmpMedicamentPrescribe(2L, "String1"), createKmpMedicamentPrescribe(3L, "String2"), createKmpMedicamentPrescribe(4L, "String2"), createKmpMedicamentPrescribe(5L, "String2"), createKmpMedicamentPrescribe(6L, "String1"), createKmpMedicamentPrescribe(7L, "String2"), createKmpMedicamentPrescribe(8L, "String2"), createKmpMedicamentPrescribe(9L, "String2"));
		List<UpdateBean> updateBeanList = mainHandler.getUpdateBeanList(kmpMedicamentPrescribeList);
		Assert.assertTrue(updateBeanList.size() == 4);
		Assert.assertTrue(updateBeanList.get(0) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(0)).getId().equals(1L) && ((RangeUpdateBean) updateBeanList.get(0)).getId2().equals(2L));
		Assert.assertTrue(updateBeanList.get(1) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(1)).getId().equals(3L) && ((RangeUpdateBean) updateBeanList.get(1)).getId2().equals(5L));
		Assert.assertTrue(updateBeanList.get(3) instanceof UpdateBean && updateBeanList.get(3).getId().equals(6L));
		Assert.assertTrue(updateBeanList.get(2) instanceof RangeUpdateBean && ((RangeUpdateBean) updateBeanList.get(2)).getId().equals(7L) && ((RangeUpdateBean) updateBeanList.get(2)).getId2().equals(9L));
	}

	private KmpMedicamentPrescribe createKmpMedicamentPrescribe(Long id, String alert) {
		KmpMedicamentPrescribe kmpMedicamentPrescribe = new KmpMedicamentPrescribe(id, null, new Date(), null);
		kmpMedicamentPrescribe.setAlert(alert);
		return kmpMedicamentPrescribe;
	}
}

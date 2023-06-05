package com.intelligent.bot.service.mj;


import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.MjTask;
import eu.maxschuster.dataurl.DataUrl;

import java.util.List;

public interface TaskService {

	B<Void> submitImagine(MjTask task);

	B<Void> submitUpscale(MjTask task, String targetMessageId, String targetMessageHash, int index);

	B<Void> submitVariation(MjTask task, String targetMessageId, String targetMessageHash, int index);

	B<Void> submitDescribe(MjTask task, DataUrl dataUrl);

	B<Void> submitBlend(MjTask task, List<DataUrl> dataUrls);
}
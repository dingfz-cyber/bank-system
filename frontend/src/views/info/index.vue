<template>
  <div class="page info-page">
    <h2 class="page-title">信息公开</h2>
    <CompanyOverview />
    <OrgStructure />
    <BusinessScope />
    <ContactInfo />
    <ServiceCommitment />
    <RiskNotice />
  </div>
</template>

<script setup>
import CompanyOverview from '@/components/info/CompanyOverview.vue'
import OrgStructure from '@/components/info/OrgStructure.vue'
import BusinessScope from '@/components/info/BusinessScope.vue'
import ContactInfo from '@/components/info/ContactInfo.vue'
import ServiceCommitment from '@/components/info/ServiceCommitment.vue'
import RiskNotice from '@/components/info/RiskNotice.vue'
</script>

<style scoped>
.info-page {
  max-width: var(--card-max-width);
  margin: 0 auto;
}
</style>
